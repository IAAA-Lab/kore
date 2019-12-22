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
package es.iaaa.kore.transform.rules

import com.andreapivetta.kolor.green
import com.andreapivetta.kolor.lightYelllow
import com.andreapivetta.kolor.yellow
import es.iaaa.kore.*
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class Report : Transformation {

    override fun process(target: Model) {
        println("Report".lightYelllow())
        println("- Selected packages:".yellow())
        println(selectedPackages(target).prependIndent("  - "))
        println("- Relevant content:".yellow())
        println(relevantContent(target).prependIndent("  "))
        println("- All relevant content:".yellow())
        println(allRelevantContent(target).prependIndent("  "))
    }

    private fun selectedPackages(target: Model): String = target
        .selectedPackages()
        .joinToString(separator = "\n") { "Package \"${it.name ?: "<<missing>>"}\" " }

    private fun relevantContent(target: Model): String = target
        .relevantContent()
        .mapNotNull { it.asString() }.joinToString(separator = "\n")

    private fun allRelevantContent(target: Model): String = target
        .allRelevantContent()
        .mapNotNull { it.asString() }.joinToString(separator = "\n")

    private fun KoreObject.asString() = when (this) {
        is KorePackage -> "- Package \"${name()}\""
        is KoreClass -> "- Class \"${parentName()}\"::\"${name()}\""
        is KoreDataType -> "- Datatype \"${parentName()}\"::\"${name()}\""
        is KoreAnnotation -> null
        is KoreAttribute -> "- Attribute \"${grandParentName()}\"::\"${parentName()}\"#\"${name()}\""
        is KoreReference -> "- Reference \"${grandParentName()}\"::\"${parentName()}\"#\"${name()}\""
        else -> throw Exception("Add support to $this")
    }

    private fun KoreNamedElement.name(): String = (name ?: "missing").yellow()
    private fun KoreNamedElement.parentName(): String =
        ((container as? KoreNamedElement)?.name ?: "missing").green()

    private fun KoreNamedElement.grandParentName(): String =
        ((container?.container as? KoreNamedElement)?.name ?: "missing").green()
}

fun Transformations.report() {
    add(Report())
}
