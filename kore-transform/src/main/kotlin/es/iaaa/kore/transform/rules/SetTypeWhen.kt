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

internal class SetTypeWhen(
    val type: KoreClassifier,
    val predicate: (KoreTypedElement) -> Boolean,
    val preset: (KoreTypedElement) -> Unit
) : Transformation {

    override fun process(target: Model) {
        val from = target.allRelevantContent()
        from.filterIsInstance<KoreTypedElement>().filter(predicate).forEach {
            preset(it)
            it.type = type
        }
    }
}

fun Transformations.setTypeWhen(
    type: KoreClassifier,
    predicate: (KoreTypedElement) -> Boolean,
    preset: (KoreTypedElement) -> Unit = {}
) {
    add(SetTypeWhen(type, predicate, preset))
}
