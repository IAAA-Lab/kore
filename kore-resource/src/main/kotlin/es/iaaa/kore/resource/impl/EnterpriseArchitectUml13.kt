/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.resource.impl

import es.iaaa.kore.*
import es.iaaa.kore.resource.Factory
import es.iaaa.kore.resource.Resource
import es.iaaa.kore.resource.ResourceHelper
import org.jdom2.Attribute
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import org.jdom2.filter.Filters
import org.jdom2.input.SAXBuilder
import org.jdom2.xpath.XPathFactory
import java.io.File

object EnterpriseArchitectUml13Factory : Factory {
    override fun createResource(file: File, resourceHelper: ResourceHelper): Resource {
        val builder = SAXBuilder()
        val document = runCatching { builder.build(file) }.getOrElse {
            return EnterpriseArchitectUml13Resource(
                isLoaded = false,
                errors = mutableListOf("Can't load document: ${it.message}")
            )
        }
        val helper = EnterpriseArchitectUml13FactoryHelper(resourceHelper)
        val rootElement = document.rootElement
        val modelXPath = "/XMI/XMI.content/*[local-name()='Model']"
        val ns = Namespace.getNamespace("omg.org/UML1.3")
        val modelPath = XPathFactory.instance().compile(modelXPath, Filters.element())
        val packages = modelPath.evaluate(rootElement).map {
            with(helper) {
                KoreModel.createPackage().apply {
                    fillPackage(it, ns)
                }
            }
        }
        val taggedValueXPath = "/XMI//*[local-name()='TaggedValue' and @modelElement]"
        val taggedValuePath = XPathFactory.instance().compile(taggedValueXPath, Filters.element())
        taggedValuePath.evaluate(rootElement).map {
            val target = helper.resolved[it.getAttribute("modelElement").value.takeLast(36)] as? KoreModelElement
            val tag = it.getAttribute("tag")?.value ?: "<<missing>>"
            val value = it.getAttribute("value")?.value ?: "<<missing>>"
            target?.apply {
                getAnnotation()?.details?.put(tag, value)
            }
        }

        helper.fixReferences()
        helper.fixTaggedStereotypes(packages)



        return EnterpriseArchitectUml13Resource(
            document = document,
            contents = packages.toMutableList(),
            isLoaded = true,
            errors = helper.errors,
            warnings = helper.warnings
        )
    }
}

data class EnterpriseArchitectUml13Resource(
    val document: Document? = null,
    override val contents: MutableList<KoreObject> = mutableListOf(),
    override val isLoaded: Boolean = true,
    override val errors: MutableList<String> = mutableListOf(),
    override val warnings: MutableList<String> = mutableListOf()
) : Resource

class EnterpriseArchitectUml13FactoryHelper(
    private val resourceHelper: ResourceHelper
) {

    private fun KoreAnnotation.fillDetails(element: Element, ns: Namespace) {
        val newDetails = element.getChildren("TaggedValue", ns)
            .map { Pair(it.getAttribute("tag")?.value, it.getAttribute("value")?.value) }
            .filterIsInstance<Pair<String, String>>().toMap()
        details.clear()
        details.putAll(newDetails)
    }

    val resolved: MutableMap<String, KoreObject> = mutableMapOf()
    private val missingStereotypes: MutableList<Pair<KoreModelElement, KoreClassifier>> = mutableListOf()
    private val missingTypes: MutableList<Pair<KoreTypedElement, KoreClassifier>> = mutableListOf()
    private val pendingGeneralizations: MutableList<Pair<String, String>> = mutableListOf()
    private val pendingRealizations: MutableList<Pair<String, String>> = mutableListOf()
    private val pendingAssociations: MutableList<Pair<Element, Namespace>> = mutableListOf()
    private val knownNamedTypes: MutableMap<String, MutableList<KoreClassifier>> = mutableMapOf()
    val errors: MutableList<String> = mutableListOf()
    val warnings: MutableList<String> = mutableListOf()

    private fun KoreObject.fillObject(element: Element) {
        element.getAttribute("xmi.id")?.asId?.let {
            id = resourceHelper.alias.getOrDefault(it, it)
            isLink = false
            resolved[it] = this
        }
        element.getAttribute("xmi.idref")?.asId?.let {
            id = resourceHelper.alias.getOrDefault(it, it)
            isLink = true
        }
    }

    private fun KoreModelElement.fillModelElement(element: Element, ns: Namespace) {
        fillObject(element)
        if (!isLink) {
            val children =
                element.getChildren("ModelElement.stereotype", ns)
                    .flatMap { it.getChildren("Stereotype", ns) }
                    .map { koreClass { fillClass(it, ns) } }
            missingStereotypes += children.filter { it.isLink }.map { Pair(this, it) }

            val tagVal = element.getChildren("ModelElement.taggedValue", ns).firstOrNull()

            if (children.isNotEmpty() || tagVal != null) {
                koreAnnotation {
                    tagVal?.let { fillDetails(it, ns) }
                    references.addAll(children)
                }
            }
        }
    }

    private fun KoreNamedElement.fillNamedElement(element: Element, ns: Namespace) {
        fillModelElement(element, ns)
        if (!isLink) {
            name = element.getAttribute("name")?.value
        }
    }

    private fun KoreClassifier.fillClassifier(element: Element, ns: Namespace) {
        fillNamedElement(element, ns)
        knownNamedTypes.putIfAbsent(this.name ?: "", mutableListOf())
        knownNamedTypes[this.name ?: ""]?.add(this)
    }

    fun KorePackage.fillPackage(element: Element, ns: Namespace) {
        fillNamedElement(element, ns)
        if (!isLink) {
            element.getChildren("Namespace.ownedElement", ns)
                .flatMap { it.children }
                .map {
                    when (val obj = toObject(it, ns)) {
                        is KoreClassifier -> obj.container = this
                        is KorePackage -> obj.superPackage = this
                    }
                }
        }
    }

    private fun KoreClass.fillClass(element: Element, ns: Namespace) {
        fillClassifier(element, ns)
        if (!isLink) {
            isInterface = element.name == "Interface"
            isAbstract = element.getAttribute("isAbstract")?.value?.toBoolean() ?: false
            element.getChildren("Classifier.feature", ns)
                .flatMap { it.children }
                .map {
                    when (val obj = toObject(it, ns)) {
                        is KoreAttribute -> obj.containingClass = this
                        is KoreOperation -> obj.containingClass = this
                    }
                }
        }
    }

    private fun KoreDataType.fillDataType(element: Element, ns: Namespace) {
        fillClassifier(element, ns)
    }

    private fun KoreModelElement.tagToInt(element: Element, name: String, default: Int, unspecified: Int): Int =
        findTaggedValue(name)?.let { value ->
            runCatching { value.toInt() }.getOrElse {
                if (value == "*") {
                    KoreTypedElement.UNBOUNDED_MULTIPLICITY
                } else {
                    errors.add("$element has unspecified multiplicity in $name tag with [$value], using [$unspecified]")
                    unspecified
                }
            }
        } ?: default

    private fun KoreTypedElement.fillTypedElement(element: Element, ns: Namespace) {
        fillNamedElement(element, ns)
        if (!isLink) {
            lowerBound = tagToInt(element, "lowerBound", 0, KoreTypedElement.UNSPECIFIED_MULTIPLICITY)
            upperBound = tagToInt(element, "upperBound", 1, KoreTypedElement.UNSPECIFIED_MULTIPLICITY)
            type = element.getChildren("StructuralFeature.type", ns)
                .flatMap { it.getChildren("Classifier", ns) }
                .map {
                    koreClassifier {
                        fillClassifier(it, ns)
                    }
                }.firstOrNull()?.also {
                    if (it.isLink) {
                        missingTypes.add(Pair(this, it))
                    }
                }
        }
    }

    private fun KoreTypedElement.fillTypedElement(element: Element, ns: Namespace, isAttribute: Boolean) {
        fillNamedElement(element, ns)
        if (!isLink) {
            lowerBound = tagToInt(element, "lowerBound", 0, KoreTypedElement.UNSPECIFIED_MULTIPLICITY)
            upperBound = tagToInt(element, "upperBound", 1, KoreTypedElement.UNSPECIFIED_MULTIPLICITY)
            type = element.getChildren("StructuralFeature.type", ns)
                .flatMap { it.getChildren("Classifier", ns) }
                .map {
                    if (isAttribute) {
                        koreDataType { fillDataType(it, ns) }
                    } else {
                        koreClass { fillClass(it, ns) }
                    }
                }.firstOrNull()?.also {
                    if (it.isLink) {
                        missingTypes.add(Pair(this, it))
                    }
                }
        }
    }

    private fun KoreOperation.fillOperation(element: Element, ns: Namespace) {
        fillTypedElement(element, ns)
        if (!isLink) {
            element.getChildren("BehavioralFeature.parameter", ns)
                .flatMap { it.getChildren("Parameter", ns) }
                .map {
                    val result = KoreModel.createParameter()
                    result.fillParameter(it, ns)
                    result
                }
                .forEach { it.operation = this }
        }
    }

    private fun KoreParameter.fillParameter(element: Element, ns: Namespace) {
        fillTypedElement(element, ns)
        if (!isLink) {
            type = element.getChildren("Parameter.type", ns)
                .flatMap { it.getChildren("Classifier", ns) }
                .map {
                    val result = KoreModel.createClassifier()
                    result.fillClassifier(it, ns)
                    result
                }
                .firstOrNull()
        }
    }

    private fun KoreStructuralFeature.fillStructuralFeature(element: Element, ns: Namespace, isAttribute: Boolean) {
        fillTypedElement(element, ns, isAttribute)
        if (!isLink) {
            isChangeable = element.getAttribute("changeable")?.value?.toBoolean() ?: true
            isUnsettable = findTaggedValue("derived") == "1"
            defaultValueLiteral = element.getChildren("Attribute.initialValue", ns)
                .flatMap { it.getChildren("Expression", ns) }
                .mapNotNull { it.getAttribute("body")?.value }
                .firstOrNull()?.let {
                    val trimmed = it.trim { c -> c == '"' }
                    if (trimmed.length == it.length) it else {
                        warnings.add("$name initial value trimmed from [$it] to [$trimmed]")
                        trimmed
                    }
                }
        }
    }

    private fun KoreAttribute.fillAttribute(element: Element, ns: Namespace) {
        fillStructuralFeature(element, ns, true)
    }

    private fun KoreReference.fillReference(element: Element, ns: Namespace) {
        fillStructuralFeature(element, ns, false)
        if (!isLink) {
            isContainement = element.getAttribute("aggregation")?.value != "composite"
            isNavigable = element.getAttribute("isNavigable")?.value?.toBoolean() ?: false
            lowerBound = 0
            upperBound = KoreTypedElement.UNBOUNDED_MULTIPLICITY
            element.getAttribute("type")?.asId?.let {
                type = koreClass {
                    id = resourceHelper.alias.getOrDefault(it, it)
                    isLink = true
                }
            }
            fillReferenceBounds(element)
        }
    }

    private fun KoreReference.fillRangeBound(result: MatchResult) {
        lowerBound = result.groupValues[1].toInt()
        upperBound = when {
            result.groupValues[2] == "" -> lowerBound
            result.groupValues[3] == "*" -> KoreTypedElement.UNBOUNDED_MULTIPLICITY
            else -> result.groupValues[3].toInt()
        }
    }

    private fun KoreReference.fillReferenceBounds(element: Element) {
        when (val value = element.getAttribute("multiplicity")?.value) {
            null -> warnings.add("$element has no multiplicity attribute interpreted as [0..1]")
            "*" -> {
                upperBound = KoreTypedElement.UNBOUNDED_MULTIPLICITY
                warnings.add("$element has multiplicity attribute with value [*] interpreted as [0..*]")
            }
            else -> {
                val result = "([0-9]+)(\\.\\.([0-9]+|\\*))?".toRegex().matchEntire(value)
                result?.let { fillRangeBound(it) }
                if (result == null) errors.add("$element has multiplicity attribute with value [$value]")
            }
        }
    }

    private fun toObject(element: Element, ns: Namespace): KoreObject? {
        return when (element.name) {
            "Class", "Interface", "Stereotype" -> {
                val result = KoreModel.createClass()
                result.fillClass(element, ns)
                result
            }
            "Package", "Model" -> {
                val result = KoreModel.createPackage()
                result.fillPackage(element, ns)
                result
            }
            "DataType" -> {
                val result = KoreModel.createDataType()
                result.fillDataType(element, ns)
                result
            }
            "Attribute" -> {
                val result = KoreModel.createAttribute()
                result.fillAttribute(element, ns)
                result
            }
            "Operation" -> {
                val result = KoreModel.createOperation()
                result.fillOperation(element, ns)
                result
            }
            "Generalization" -> {
                pendingGeneralizations += Pair(
                    element.getAttribute("subtype").asId,
                    element.getAttribute("supertype").asId
                )
                null
            }
            "Dependency" -> {
                val result = KoreModel.createClass()
                result.fillClass(element, ns)
                if (result.findTaggedValue("ea_type") == "Realisation") {
                    pendingRealizations += Pair(
                        element.getAttribute("client").asId,
                        element.getAttribute("supplier").asId
                    )
                }
                null
            }
            "Association" -> {
                pendingAssociations += Pair(element, ns)
                null
            }
            else -> {
                warnings.add("$element is not processed")
                null
            }
        }
    }

    fun fixReferences() {
        fixStereotypes()
        fixTypes()
        fixGeneralizations()
        fixAssociations()
        fixRealizations()
    }

    private fun fixStereotypes() {
        missingStereotypes.forEach { (target, stereoRef) ->
            val stereotype = resolved[stereoRef.id] as? KoreClass?
            if (stereotype != null) {
                val refs = target.getAnnotation()?.references
                refs?.remove(stereoRef)
                refs?.add(stereotype)
                stereoRef.container = null
            } else {
                warnings.add("$target has an unresolved stereotype with id ${stereoRef.id}")
            }
        }
    }

    private fun fixTypes() {

        missingTypes.forEach { (target, typeRef) ->
            val type = resolved[typeRef.id] as? KoreClassifier?
            when {
                type?.id?.startsWith("eaxmiid") == true -> {
                    val candidates = knownNamedTypes[type.name ?: ""]?.filter { it.id != type.id } ?: mutableListOf()
                    when (candidates.size) {
                        0 -> target.type = null
                        1 -> {
                            target.type = candidates[0]
                            typeRef.container = null
                        }
                        else -> {
                            val (selectedType, selectWarnings) = resourceHelper.selectType(target, candidates)
                            target.type = selectedType
                            typeRef.container = null
                            warnings.addAll(selectWarnings)
                        }
                    }
                }
                type != null -> {
                    target.type = type
                    typeRef.container = null
                }
                else -> {
                    warnings.add("$target has an unresolved type with id ${typeRef.id}")
                }
            }
        }
    }

    private fun fixGeneralizations() {
        pendingGeneralizations.forEach { (subtypeId, supertypeId) ->
            val subtype = resolved[subtypeId] as? KoreClass?
            val supertype = resolved[supertypeId] as? KoreClass?
            if (subtype != null && supertype != null) {
                subtype.superTypes.add(supertype)
            } else {
                if (subtype == null) {
                    warnings.add("id $subtypeId as subtype in a generalization is unresolved")
                }
                if (supertype == null) {
                    warnings.add("id $supertypeId as supertype in a generalization is unresolved")
                }
            }
        }
    }

    private fun fixRealizations() {
        pendingRealizations.forEach { (clientId, supplierId) ->
            val client = resolved[clientId]
            val supplier = resolved[supplierId] as? KoreClass?
            if (client != null && supplier != null) {
                client.metaClass = supplier
            } else {
                if (client == null) {
                    warnings.add("id $clientId as client in a realisation is unresolved")
                }
                if (supplier == null) {
                    warnings.add("id $supplierId as supertype in a realisation is unresolved")
                }
            }
        }
    }

    private fun fixAssociations() {
        pendingAssociations.forEach { (element, namespace) ->
            element.getChildren("Association.connection", namespace)
                .map { it.getChildren("AssociationEnd", namespace) }
                .map { list ->
                    val result = KoreModel.createReference()
                    result.fillReference(list[0], namespace)
                    val result1 = KoreModel.createReference()
                    result1.fillReference(list[1], namespace)
                    val type1 = resolved[result.type?.id] as? KoreClass?
                    val type2 = resolved[result1.type?.id] as? KoreClass?
                    if (type1 != null && type2 != null) {
                        result.type = type1
                        result1.type = type2
                        result.containingClass = type2
                        result1.containingClass = type1
                        result.opposite = result1
                        result1.opposite = result
                    } else {
                        if (type1 == null) {
                            warnings.add("${list[0]} type cannot be resolved")
                        }
                        if (type2 == null) {
                            warnings.add("${list[1]} type cannot be resolved")
                        }
                    }
                }
        }
    }

    internal fun fixTaggedStereotypes(list: List<KorePackage>) {
        fun KoreModelElement.fixStereotypeFrom(tag: String) {
            val refs = getAnnotation()?.references
            val namedRefs = refs?.filterIsInstance<KoreNamedElement>()
            findTaggedValue(tag)?.let { candidate ->
                if (namedRefs?.find { it.name == candidate } == null) {
                    val stereotype = koreClass {
                        name = candidate
                    }
                    // FIXME class without container
                    refs?.add(stereotype)
                }
            }
        }

        fun KoreModelElement.fixStereotypeFromXref() {
            val regex = "@STEREO;Name=([^;]+);".toRegex()
            val refs = getAnnotation()?.references
            val namedRefs = refs?.filterIsInstance<KoreNamedElement>()
            findTaggedValue("\$ea_xref_property")?.let { property ->
                regex.findAll(property).map { match -> match.groupValues[1] }.forEach { candidate ->
                    if (namedRefs?.find { it.name == candidate } == null) {
                        val stereotype = KoreModel.createClass()
                        stereotype.name = candidate
                        // FIXME class without container
                        refs?.add(stereotype)
                    }
                }
            }
        }
        list.flatMap { it.allContents().toList() }.filterIsInstance<KoreModelElement>().forEach { element ->
            element.fixStereotypeFrom("stereotype")
            element.fixStereotypeFrom("destStereotype")
            element.fixStereotypeFrom("sourceStereotype")
            element.fixStereotypeFromXref()
        }
    }
}

val Attribute.asId: String
    get() = value.takeLast(36)


