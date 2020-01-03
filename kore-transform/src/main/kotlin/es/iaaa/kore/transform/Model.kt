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
package es.iaaa.kore.transform

import es.iaaa.kore.*
import es.iaaa.kore.impl.Validable
import es.iaaa.kore.impl.Violations
import es.iaaa.kore.resource.Resource
import es.iaaa.kore.resource.ResourceFactory
import es.iaaa.kore.resource.allContents
import java.io.File

class Model(val input: Input) : Validable {

    private lateinit var resource: Resource

    override fun validate(): Violations {
        TODO("not implemented")
    }

    fun load() {
        with(input) {
            resource = ResourceFactory.createResource(File(file.get()), type.get(), helper.get())
        }
    }

    fun selectedPackages(): List<KorePackage> =
        resource.allContents().filterIsInstance<KorePackage>().filter { input.selector.get()(it) }.toList()

    fun relevantContent(): List<KoreObject> =
        (selectedPackages() + selectedPackages().flatMap { it.allContents().toList() }) + tracked

    fun allRelevantContent(): List<KoreObject> = relevantContent().toSet().typeClosure(input.boundary.get()).toList()

    fun allDependencies(): List<KoreObject> = allRelevantContent() - relevantContent()

    fun allContent(): List<KoreObject> = resource.allContents().toList()

    val tracked: MutableList<KoreObject> = mutableListOf()
}

fun Set<KoreObject>.typeClosure(boundary: (KoreObject, KoreObject) -> Boolean = { _, _ -> true }): Set<KoreObject> {

    fun List<KoreObject>.metas(): List<KoreObject> =
        filterIsInstance<KoreModelElement>().mapNotNull { src -> src.metaClass }

    fun List<KoreObject>.refinements(): List<KoreObject> =
        filterIsInstance<KoreModelElement>().flatMap { src ->
            src.annotations.flatMap { ann -> ann.references }
        }

    fun List<KoreObject>.annotations(): List<KoreObject> =
        filterIsInstance<KoreModelElement>().flatMap { src ->
            src.annotations
        }

    fun List<KoreObject>.attributes(): List<Pair<KoreObject, KoreObject>> =
        filterIsInstance<KoreClass>().flatMap { src -> src.allAttributes().map { Pair(src, it) } }

    fun List<KoreObject>.references(): List<Pair<KoreObject, KoreObject>> =
        filterIsInstance<KoreClass>().flatMap { src -> src.allReferences().filter { it.isNavigable }.map { Pair(src, it) } }

    fun Pair<KoreObject, KoreObject>.types(): List<Pair<KoreObject, KoreObject>> {
        val second = this.second
        return if (second is KoreTypedElement) {
            val type = second.type
            if (type == null) {
                listOf(this)
            } else {
                listOf(this, Pair(second, type))
            }
        } else {
            listOf(this)
         }
    }

    tailrec fun expand(candidates: Set<KoreObject>, sources: List<KoreObject>): Set<KoreObject> {
        val reachablesWithProvenance = with(sources) { attributes() + references() }
            .flatMap { it.types() }.filterNot { it.second in candidates }
        val reachablesWithoutProvenance = with(sources) { annotations() + refinements() + metas() }.filterNot { it in candidates }
        val reachables = reachablesWithProvenance.map { it.second } + reachablesWithoutProvenance
        return if (reachables.isEmpty()) candidates else expand(
            candidates = candidates + reachables,
            sources = reachablesWithProvenance.filter { boundary(it.first, it.second) }.map { it.second } + reachablesWithoutProvenance
        )
    }
    return expand(this, toList())
}
