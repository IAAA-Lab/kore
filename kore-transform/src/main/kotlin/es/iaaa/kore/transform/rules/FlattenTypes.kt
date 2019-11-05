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

import es.iaaa.kore.*
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class FlattenTypes(
    val predicate: (KoreNamedElement) -> Boolean,
    val postFlatten: (KoreTypedElement, KoreTypedElement) -> Unit,
    val global: Boolean,
    private val maxIterations: Int = 3
) : Transformation {

    override fun process(target: Model) {
        var iterations = 0
        do {
            var changes = 0
            var content = if (global) target.allContent() else target.allRelevantContent()
            content.filterIsInstance<KoreClass>().forEach { cls ->
                val toFlatten = cls.attributes.filter { ref ->
                    with(ref) {
                        upperBound == 1 &&
                        type?.let { type -> predicate(type) } == true &&
                        (type as? KoreClass)?.allReferences()?.isEmpty() == true &&
                        (type as? KoreClass)?.allAttributes()?.all { att -> att.upperBound == 1 } == true
                    }
                }
                val atts = cls.attributes
                atts.forEach { it.containingClass = null }
                atts.forEach { att ->
                    if (att in toFlatten) {
                        (att.type as KoreClass).allAttributes().forEach { newAtt ->
                            postFlatten(att, newAtt.copy(cls))
                        }
                        changes++
                    } else {
                        att.containingClass = cls
                    }
                }
            }
            iterations++
        } while (changes > 0 && iterations < maxIterations)
    }
}

fun Transformations.flattenTypes(predicate: (KoreNamedElement) -> Boolean,
                                 postFlatten: (KoreTypedElement, KoreTypedElement) -> Unit,
                                 global: Boolean = false) {
    add(FlattenTypes(predicate, postFlatten, global))
}
