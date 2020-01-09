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

import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KoreTypedElement
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class MapEntry(
    val type: KoreClassifier,
    val predicate: (KoreTypedElement) -> Boolean,
    val preset: (KoreTypedElement) -> Unit,
    val postset: (KoreTypedElement) -> Unit
) : Transformation {

    override fun process(target: Model) {
        val from = target.allContent()
        from.filterIsInstance<KoreTypedElement>().filter(predicate).forEach {
            preset(it)
            it.type = type
            postset(it)
        }
    }
}

fun Transformations.mapEntry(
    type: String? = null,
    typePredicate: (KoreTypedElement) -> Boolean = { true },
    preset: (KoreTypedElement) -> Unit = {},
    postset: (KoreTypedElement) -> Unit = {},
    targetType: KoreClassifier
) {
    add(MapEntry(targetType, type?.let { { e: KoreTypedElement -> e.type?.name == it} } ?: typePredicate, preset, postset))
}
