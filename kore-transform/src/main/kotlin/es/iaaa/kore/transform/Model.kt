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

    fun allRelevantContent(): List<KoreObject> = relevantContent().toSet().typeClosure().toList()

    fun allDependencies(): List<KoreObject> = allRelevantContent() - relevantContent()

    fun allContent(): List<KoreObject> = resource.allContents().toList()

    val tracked: MutableList<KoreObject> = mutableListOf()
}

fun Set<KoreObject>.typeClosure(): Set<KoreObject> {

    fun Set<KoreObject>.refinements(): List<KoreObject> =
        filterIsInstance<KoreModelElement>().flatMap {
            it.annotations.flatMap { ann -> ann.references }
        }

    fun Set<KoreObject>.attributes(): List<KoreObject> =
        filterIsInstance<KoreClass>().flatMap(KoreClass::allAttributes)

    fun Set<KoreObject>.references(): List<KoreObject> =
        filterIsInstance<KoreClass>().flatMap(KoreClass::allReferences).filter { ref -> ref.name != null }

    fun Set<KoreObject>.types(): List<KoreObject> =
        filterIsInstance<KoreTypedElement>().filter { it.name != null }.mapNotNull(KoreTypedElement::type)

    tailrec fun expand(base: Set<KoreObject>): Set<KoreObject> {
        val preExpanded = base + base.refinements() + base.attributes() + base.references()
        val expanded = preExpanded + preExpanded.types()
        return if (expanded == base) base else expand(expanded)
    }
    return expand(toSet())
}