/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.iaaa.kore.util

import es.iaaa.kore.*

/**
 * Pretty print package name
 */
fun KorePackage.toPackageName(
    relevantList: List<KoreObject>,
    parentName: String = "",
    separator: String = "::"
): String = listOfNotNull(
    metaClass?.name ?: "Package",
    prettyPrintStereotypes(relevantList),
    name?.let { "\"${if (parentName.isNotEmpty()) "$parentName$separator" else ""}$it\"" }
).filter { it.isNotBlank() }.joinToString(separator = " ")

/**
 * Pretty print package body
 */

fun KorePackage.toPackageBody(relevantList: List<KoreObject>): String =
    listOf(
        prettyPrintMetaStructuralFeatures(relevantList),
        prettyPrintAnnotations(relevantList),
        prettyPrintClassifiers(relevantList)
    )
        .filter { it.isNotEmpty() }
        .joinToString(separator = "\n")

fun KoreModelElement.prettyPrintAnnotations(relevantList: List<KoreObject>): String =
    annotations.filter { it in relevantList || relevantList.isEmpty() }.flatMap {
        it.details.map { (key, value) -> "$key = \"$value\"" }.sorted()
    }.joinToString(separator = "\n")

/**
 * Formatted as a pretty string.
 */
fun KoreAnnotation.toPrettyString(): String =
    details.map { (key, value) -> "$key = \"$value\"" }.sorted().joinToString(separator = "\n")

/**
 *  Pretty Print Package
 */
fun KorePackage.toPrettyString(
    relevantList: List<KoreObject> = emptyList(),
    parentName: String = "",
    separator: String = "::"
): String =
    with(StringBuilder()) {
        val body = toPackageBody(relevantList)
        if (body.isNotEmpty()) {
            append(toPackageName(relevantList, parentName, separator))
            append(" {")
            appendln()
            append(body.prependIndent("  "))
            appendln()
            append("}")
        }
        if (subpackages.isNotEmpty()) {
            subpackages.filter { it in relevantList || relevantList.isEmpty() }
                .joinTo(this, prefix = "\n", separator = "\n") {
                    it.toPrettyString(
                        relevantList, name ?: "", separator
                    )
                }
        }
        toString()
    }

/**
 *  Pretty Print classifier
 */
private fun KorePackage.prettyPrintClassifiers(relevantList: List<KoreObject>): String =
    classifiers.filter { relevantList.isEmpty() || it in relevantList }.mapNotNull { cls ->
        when (cls) {
            is KoreClass -> cls.toPrettyString(relevantList)
            is KoreDataType -> cls.toPrettyString()
            else -> null
        }
    }.joinToString(separator = "\n")

/**
 * Pretty print class name
 */
fun KoreClass.toClassName(relevantList: List<KoreObject>): String =
    listOf(
        if (isAbstract) "Abstract" else "",
        metaClass?.name ?: "Class",
        prettyPrintStereotypes(relevantList),
        "\"$name\""
    ).filter { it.isNotBlank() }.joinToString(separator = " ")

/**
 * Pretty print class body.
 */

fun KoreClass.toClassBody(relevantList: List<KoreObject>): String =
    listOf(
        prettyPrintMetaStructuralFeatures(relevantList),
        prettyPrintAnnotations(relevantList),
        prettyPrintNamedStructuralFeatures(relevantList)
    )
        .filter { it.isNotEmpty() }
        .joinToString(separator = "\n")

/**
 * Print named structural features
 */
fun KoreClass.prettyPrintNamedStructuralFeatures(relevantList: List<KoreObject>): String {
    val sorted =
        allAttributes().sortedWith(StructuralFeatureComparator) + allReferences().sortedWith(StructuralFeatureComparator)
    return sorted.filter { !it.name.isNullOrBlank()} .map { feature ->
        when (feature) {
            is KoreReference -> feature.toPrettyString(relevantList)
            is KoreAttribute -> feature.toPrettyString(relevantList)
            else -> null
        }
    }.joinToString(separator = "\n")
}

/**
 * Print structural features
 */
fun KoreObject.prettyPrintMetaStructuralFeatures(relevantList: List<KoreObject>): String {
    val mc = metaClass
    return if (mc != null && (relevantList.isEmpty() || mc in relevantList)) {
        val sorted =
            mc.allAttributes().sortedWith(StructuralFeatureComparator) + mc.allReferences().sortedWith(
                StructuralFeatureComparator
            )
        sorted.filter { runCatching { isSet(it.name ?: "") }.getOrDefault(false) }
            .mapNotNull { feature ->
                feature.name?.let { name ->
                    if (isSet(name)) {
                        when (feature) {
                            is KoreReference -> feature.toPrettyString(relevantList, get(name))
                            is KoreAttribute -> feature.toPrettyString(relevantList, get(name))
                            else -> null
                        }
                    } else null
                }
            }.map { "* $it" }.joinToString(separator = "\n")
    } else ""
}

/**
 *  Pretty Class
 */
fun KoreClass.toPrettyString(relevantList: List<KoreObject> = emptyList()): String =
    with(StringBuilder()) {
        append(toClassName(relevantList))
        val body = toClassBody(relevantList)
        append(" {")
        if (body.isNotEmpty()) {
            appendln()
            append(body.prependIndent("  "))
            appendln()
        }
        append("}")
        toString()
    }

/**
 *  Pretty DataType
 */
fun KoreDataType.toPrettyString(): String =
    with(StringBuilder()) {
        appendln("Datatype $name")
        toString()
    }

fun KoreNamedElement.metaToStringOrDefault(relevantList: List<KoreObject> = emptyList(), vararg strings: String?): String {
    val base = metaClass?.toString?.let {
        val prefix = prettyPrintStereotypes(relevantList)
        val suffix = it(this)
        if (prefix.isBlank()) suffix else "$prefix $suffix"
    }
    return base ?: strings.filter { !it.isNullOrBlank() }.joinToString(separator = " ")
}

/**
 *  Pretty Reference
 */
fun KoreReference.toPrettyString(relevantList: List<KoreObject> = emptyList(), value: Any? = defaultValue): String =
    metaToStringOrDefault(
        relevantList,
        prettyPrintStereotypes(relevantList),
        name,
        type?.name,
        if (value != null) {
            "= \"$value\""
        } else {
            prettyPrintCardinality()
        },
        "as ref"
    )

private fun KoreTypedElement.prettyPrintCardinality(): String = if (lowerBound != 1 || upperBound != 1) {
    "[$lowerBound..${if (upperBound < 0) "*" else upperBound.toString()}]"
} else ""


/**
 *  Pretty Attribute
 */
fun KoreAttribute.toPrettyString(relevantList: List<KoreObject>, value: Any? = null): String =
    metaToStringOrDefault(
        relevantList,
        prettyPrintStereotypes(relevantList),
        name,
        type?.name?.let { ": $it" },
        if (value != null) {
            "= \"$value\""
        } else {
            prettyPrintCardinality()
        }
    )

/**
 * Stereotypes.
 */
fun KoreModelElement.prettyPrintStereotypes(relevantList: List<KoreObject>): String = findDefaultNamedReferences()
    .filter { relevantList.isEmpty() || it in relevantList }
    .joinToString(separator = ", ") { "<<${it.name}>>" }

