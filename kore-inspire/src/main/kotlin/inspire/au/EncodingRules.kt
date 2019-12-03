@file:Suppress("ObjectPropertyName")

package inspire.au

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transformations
import es.iaaa.kore.transform.rules.*
import java.net.URL

val `simple Feature Type stereotype to GeoPackage Feature`: Transformations.(Map<String, Any>) -> Unit = {
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature("featureType"))
}

val `Feature Type stereotype without geometry to GeoPackage Attribute`: Transformations.(Map<String, Any>) -> Unit = {
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute("featureType"))
}

val `Data Type stereotype to GeoPackage Attribute`: Transformations.(Map<String, Any>) -> Unit = {
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature("dataType"))
}

val `simple feature-like Data Type stereotype to GeoPackage Feature`: Transformations.(Map<String, Any>) -> Unit = {
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute("dataType"))
}

val `general rule ISO-19103 Basic Types`: Transformations.(Map<String, Any>) -> Unit = {
    setTypeWhen(TextType(), predicate = { it.type?.name == "CharacterString" })
    setTypeWhen(BooleanType(), predicate = { it.type?.name == "Boolean" })
    setTypeWhen(IntegerType(), predicate = { it.type?.name == "Integer" })
    setTypeWhen(DoubleType(), predicate = { it.type?.name == "Real" })
    setTypeWhen(TextType(), predicate = { it.type?.name == "Decimal" })
    setTypeWhen(DateTimeType(), predicate = { it.type?.name == "DateTime" })
    setTypeWhen(DateType(), predicate = { it.type?.name == "Date" })
}

val `general rule ISO-19107 Geometry Types`: Transformations.(Map<String, Any>) -> Unit = {
    setTypeWhen(CurveType(), predicate = { it.type?.name == "GM_Curve" })
    setTypeWhen(MultiSurfaceType(), predicate = { it.type?.name == "GM_MultiSurface" })
    setTypeWhen(PointType(), predicate = { it.type?.name == "GM_Point" })
}

val `URI subrule`: Transformations.(Map<String, Any>) -> Unit = { _ ->
    setTypeWhen(TextType(), predicate = { it.type?.name == "URI" })
}

val `LocalisedCharacterString subrule`: Transformations.(Map<String, Any>) -> Unit = { _ ->
    patch<KoreClass>(predicate = {
        name == "LocalisedCharacterString"
    }) {
        attribute {
            name = "text"
            type = TextType()
            lowerBound = 1
        }
    }
    patch<KoreReference>(predicate = { type?.name == "PT_Locale" && upperBound == 1 }) {
        toAttribute()
    }
    patch<KoreAttribute>(predicate = { type?.name == "PT_Locale" && upperBound == 1 }) {
        val parent = containingClass!!
        val atts = parent.attributes
        atts.forEach { it.containingClass = null }
        atts.forEach { att ->
            if (att == this) {
                (att.type as KoreClass).allAttributes().forEach { toBeCopiedAtt ->
                    val addedAtt = toBeCopiedAtt.copy(parent)
                    addedAtt.name = toBeCopiedAtt.name
                    addedAtt.lowerBound = kotlin.math.min(toBeCopiedAtt.lowerBound, att.lowerBound)
                }
            } else {
                att.containingClass = parent
            }
        }
    }
}

val `general rule ISO-19139 Metadata XML Implementation Types`: List<Transformations.(Map<String, Any>) -> Unit> = listOf(
    `LocalisedCharacterString subrule`,
    `URI subrule`
)

val `general rule Enumeration Types`: Transformations.(Map<String, Any>) -> Unit = { options ->
    val withDescription = options["description"] == true
    val patched = mutableListOf<KoreClass>()
    patch<KoreClass>(predicate = hasRefinement("enumeration")) {
        metaClass = EnumConstraint
        attributes.forEach {
            it.metaClass = EnumConstraintValue
            if (withDescription) {
                val uri = "http://inspire.ec.europa.eu/enumeration/" + it.containingClass?.name + "/" + it.name
                if (khttp.get(uri).statusCode == 200) {
                    it.description = uri
                }
            }
        }
        patched.add(this)
    }
    setTypeWhen(TextType(), predicate = {
        it.type in patched
    }, preset = {
        val type = it.type
        if (type is KoreClass) {
            it.geoPackageSpec().add(type)
        }
    })
}

val `general rule CodeList Types`: Transformations.(Map<String, Any>) -> Unit = { options ->
    val withDescription = options["description"] == true
    val patched = mutableListOf<KoreClass>()
    patch<KoreClass>(predicate = hasRefinement("codeList")) {
        metaClass = EnumConstraint
        attributes.forEach {
            it.metaClass = EnumConstraintValue
            if (withDescription) {
                val uri = "http://inspire.ec.europa.eu/codelist/" + it.containingClass?.name + "/" + it.name
                if (khttp.get(uri).statusCode == 200) {
                    it.description = uri
                }
            }
        }
        patched.add(this)
    }
    setTypeWhen(TextType(), predicate = {
        it.type in patched
    }, preset = {
        val type = it.type
        if (type is KoreClass) {
            it.geoPackageSpec().add(type)
        }
    })
}

val `voidable properties have a min cardinality of 0`: Transformations.(Map<String, Any>) -> Unit = {
    setLowerBoundWhen(0) { it.findDefaultNamedReferences().any { ref -> ref.name == "voidable" } }
}

val `flatten Data Types with upper cardinality of 1 but Identifier`: Transformations.(Map<String, Any>) -> Unit = {
    flattenTypes(predicate = { obj-> obj.hasRefinement("dataType") && !obj.hasName("Identifier") },
        postFlatten = { old, new ->
            new.name = "${old.name}_${new.name}"
            new.lowerBound = kotlin.math.min(old.lowerBound, new.lowerBound)
        })
}

val `ensure that arrays are treated as references from now`: Transformations.(Map<String, Any>) -> Unit = {
    patch<KoreAttribute>(predicate = { type?.metaClass == AttributesTable }) {
        toReference()
    }
    patch<KoreClass>(predicate = { attributes.any { it.upperBound != 1 } }) {
        attributes.filter { it.upperBound != 1 }.forEach { it.toReference() }
    }
}

val `create supporting Attribute tables for Enumerations and Codelists involved in arrays`: Transformations.(Map<String, Any>) -> Unit = {
    patch<KoreReference>(predicate = {
        findGeoPackageSpec()?.any { Constraint.isInstance(it) } == true
    }) {
        val from = this
        val constraint = from.geoPackageSpec().first { Constraint.isInstance(it) } as KoreClass
        val target = attributes(constraint.name ?: "<<missing>>") {
            container = constraint.container
            constraint.annotations.map { it.copy(this) }
            attribute {
                name = "value"
                type = from.type
                lowerBound = 1
                geoPackageSpec().addAll(from.geoPackageSpec().filter { Constraint.isInstance(it) })
            }
        }
        from.type = target
        from.geoPackageSpec().removeIf { Constraint.isInstance(it) }
    }
}

val `load authoritative descriptions of the reasons for void values as metadata`: Transformations.(Map<String, Any>) -> Unit = {
    val definitions = listOf(
        Triple(1, "Unknown", "attribute"),
        Triple(2, "Unpopulated", "attributeType"),
        Triple(3, "Withheld", "attribute"),
        Triple(4, "Withheld", "attributeType")
    )

    var rootContainer: KorePackage? = null

    patch<KorePackage>(predicate = { name == "AdministrativeUnits" }) {
        rootContainer = this
    }

    patch<KoreClass>(predicate = { name == "VoidReasonValue" }, global = true) {
        val parentName = name
        attributes.forEach { att ->
            definitions.filter { it.second == att.name }.forEach { def ->
                rootContainer?.metadata {
                    id = def.first.toString()
                    name = def.second
                    scope = def.third
                    standardUri = "http://www.isotc211.org/2005/gmd"
                    mimeType = "text/xml"
                    val url = "http://inspire.ec.europa.eu/codelist/$parentName/${def.second}/${def.second}.en.iso19135xml"
                    metadata = URL(url).openStream().use { it.bufferedReader().readText() }
                }
            }
        }
    }
}

fun Transformations.manipulation(block: Transformations.(Map<String, Any>) -> Unit, options: Map<String, Any> = emptyMap()) {
    block(this, options)
}


fun Transformations.rule(block: Transformations.(Map<String, Any>) -> Unit, options: Map<String, Any> = emptyMap()) {
    block(this, options)
}

fun Transformations.ruleWithTrack(block: Transformations.(Map<String, Any>) -> List<KoreClass>, options: Map<String, Any> = emptyMap()) {
    track(block(this, options))
}


fun Transformations.rule(blocks: List<Transformations.(Map<String, Any>) -> Unit>, options: Map<String, Any> = emptyMap()) {
    blocks.forEach { it(this, options) }
}


private fun hasRefinement(name: String, source: String? = null): (KoreObject) -> Boolean = { obj ->
    obj.hasRefinement(name, source)
}


private fun canToFeature(name: String): (KoreObject) -> Boolean = { obj ->
    if (obj.hasRefinement(name)) {
        obj as KoreClass
        val properties = obj.attributes.filter { GeometryType.isInstance(it.type) }
        properties.size == 1 && properties.all { it.upperBound == 1 }
    } else {
        false
    }
}

private fun canToAttribute(name: String): (KoreObject) -> Boolean = { obj ->
    if (obj.hasRefinement(name)) {
        obj as KoreClass
        obj.attributes.none { GeometryType.isInstance(it.type) }
    } else {
        false
    }
}

private fun KoreObject.hasRefinement(name: String, source: String? = null): Boolean {
    return if (this is KoreModelElement) {
        getAnnotation(source)?.references?.filterIsInstance<KoreNamedElement>()?.any { it.name == name }
            ?: false
    } else false
}

private fun KoreObject.hasName(name: String): Boolean = this is KoreNamedElement && this.name == name
