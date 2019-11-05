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
fun KorePackage.toPackageName(parentName: String = "", separator: String = "::"): String =
    with(StringBuilder()) {
        append(metaClass?.name ?: "Package")
        append(prettyPrintStereotypes())
        name?.let {
            append(" \"")
            if (parentName.isNotEmpty()) {
                append("$parentName$separator")
            }
            append("$it\"")
        }
        toString()
    }

/**
 * Pretty print package body
 */

fun KorePackage.toPackageBody(): String =
    listOf(prettyPrintAnnotations(), prettyPrintClassifiers())
        .filter { it.isNotEmpty() }
        .joinToString(separator = "\n")

fun KoreModelElement.prettyPrintAnnotations(): String =
    annotations.flatMap {
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
fun KorePackage.toPrettyString(parentName: String = "", separator: String = "::"): String =
    with(StringBuilder()) {
        append(toPackageName(parentName, separator))
        val body = toPackageBody()
        append(" {")
        if (body.isNotEmpty()) {
            appendln()
            append(body.prependIndent("  "))
            appendln()
        }
        append("}")
        if (subpackages.isNotEmpty()) {
            subpackages.joinTo(this, prefix = "\n", separator = "\n") {
                it.toPrettyString(
                    name ?: "", separator
                )
            }
        }
        toString()
    }

/**
 *  Pretty Print classifier
 */
private fun KorePackage.prettyPrintClassifiers(): String =
    with(StringBuffer()) {
        classifiers.joinTo(this, separator = "\n") { cls ->
            when (cls) {
                is KoreClass -> cls.toPrettyString()
                is KoreDataType -> cls.toPrettyString()
                else -> ""
            }
        }
        toString().trim()
    }

/**
 * Pretty print class name
 */
fun KoreClass.toClassName(): String =
    with(StringBuilder()) {
        append(metaClass?.name ?: "Class")
        append(prettyPrintStereotypes())
        append(" \"$name\"")
        toString()
    }

/**
 * Pretty print class body.
 */

fun KoreClass.toClassBody(): String =
    listOf(prettyPrintAnnotations(), prettyPrintMetaStructuralFeatures(), prettyPrintStructuralFeatures())
        .filter { it.isNotEmpty() }
        .joinToString(separator = "\n")

/**
 * Print structural features
 */
fun KoreClass.prettyPrintStructuralFeatures(): String {
    val sorted =
        allAttributes().sortedWith(StructuralFeatureComparator) + allReferences().sortedWith(StructuralFeatureComparator)
    return sorted.joinToString(separator = "\n") { feature ->
        when (feature) {
            is KoreReference -> feature.toPrettyString()
            is KoreAttribute -> feature.toPrettyString()
            else -> ""
        }
    }
}

/**
 * Print structural features
 */
fun KoreClass.prettyPrintMetaStructuralFeatures(): String =
    metaClass?.let { mc ->
        val sorted =
            mc.allAttributes().sortedWith(StructuralFeatureComparator) + mc.allReferences().sortedWith(
                StructuralFeatureComparator
            )
        return sorted.filter { runCatching { isSet(it.name ?: "") }.getOrDefault(false) }
            .joinToString(separator = "\n") { feature ->
                feature.name?.let { name ->
                    if (isSet(name)) {
                        when (feature) {
                            is KoreReference -> feature.toPrettyString(get(name))
                            is KoreAttribute -> feature.toPrettyString(get(name))
                            else -> ""
                        }
                    } else ""
                } ?: ""
            }
    } ?: ""

/**
 *  Pretty Class
 */
fun KoreClass.toPrettyString(): String =
    with(StringBuilder()) {
        append(toClassName())
        val body = toClassBody()
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

fun KoreNamedElement.resolveToString(): ((KoreObject) -> String)? {
    return metaClass?.toString
}

/**
 *  Pretty Reference
 */
fun KoreReference.toPrettyString(): String =
    resolveToString()?.let { it(this) } ?: with(StringBuilder()) {
        append("ref")
        append(prettyPrintStereotypes())
        name?.let {
            if (!this.endsWith(" ")) append(" ")
            append(it)
        }
        type?.name?.let {
            name?.let { append(": ") }
            if (!this.endsWith(" ")) append(" ")
            append(it)
        }
        if (name != null && (lowerBound != 1 || upperBound != 1)) {
            append("${prettyPrintCardinality()} ")
        }
        defaultValueLiteral?.let {
            append("= $defaultValueLiteral ")
        }
        inverse(this)
        toString().trim()
    }

/**
 *  Pretty Reference
 */
fun KoreReference.toPrettyString(value: Any?): String =
    resolveToString()?.let { it(this) } ?: with(StringBuilder()) {
        append("ref")
        append(prettyPrintStereotypes())
        name?.let {
            if (!this.endsWith(" ")) append(" ")
            append(it)
        }
        type?.name?.let {
            name?.let { append(": ") }
            if (!this.endsWith(" ")) append(" ")
            append(it)
        }
        if (name != null && (lowerBound != 1 || upperBound != 1)) {
            append("${prettyPrintCardinality()} ")
        }
        value?.let {
            append("= $value ")
        }
        inverse(this)
        toString().trim()
    }

private fun KoreReference.inverse(stringBuilder: StringBuilder) {
    val ref = opposite
    ref?.name?.let {
        if (name == null) {
            stringBuilder.append(" ")
        }
        stringBuilder.append("inv $it")
        if (ref.lowerBound != 1 || ref.upperBound != 1) {
            stringBuilder.append(ref.prettyPrintCardinality())
        }
    }
}

private fun KoreReference.prettyPrintCardinality() =
    "[$lowerBound..${if (upperBound < 0) "*" else upperBound.toString()}]"

/**
 *  Pretty Attribute
 */
fun KoreAttribute.toPrettyString(): String =
    metaClass?.toString?.let { it(this) } ?: with(StringBuilder()) {
        if (findDefaultNamedReferences().isNotEmpty()) {
            append(
                findDefaultNamedReferences().map { it.name }.joinToString(
                    prefix = "<<",
                    separator = ", ",
                    postfix = ">> "
                )
            )
        }
        append("$name")
        type?.name?.let { append(": $it") }
        if (lowerBound != 1 || upperBound != 1) {
            append(" [$lowerBound..${if (upperBound < 0) "*" else upperBound.toString()}]")
        }
        toString()
    }

/**
 *  Pretty Attribute
 */
fun KoreAttribute.toPrettyString(value: Any?): String =
    resolveToString()?.let { it(this as KoreObject) } ?: with(StringBuilder()) {
        if (findDefaultNamedReferences().isNotEmpty()) {
            append(
                findDefaultNamedReferences().map { it.name }.joinToString(
                    prefix = "<<",
                    separator = ", ",
                    postfix = ">> "
                )
            )
        }
        append(name)
        value?.let {
            append(" = \"$value\"")
        }
        toString()
    }

/**
 * Stereotypes.
 */
fun KoreModelElement.prettyPrintStereotypes(): String =
    if (findDefaultNamedReferences().isNotEmpty()) {
        findDefaultNamedReferences().map { it.name }.joinToString(prefix = " <<", separator = ", ", postfix = ">> ")
    } else ""
