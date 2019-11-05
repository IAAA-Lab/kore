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

import es.iaaa.kore.KoreTypedElement
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class SetLowerBoundWhen(
    val lowerBound: Int,
    val predicate: (KoreTypedElement) -> Boolean
) : Transformation {

    override fun process(target: Model) {
        target.allRelevantContent().filterIsInstance<KoreTypedElement>().filter(predicate)
            .forEach { it.lowerBound = lowerBound }
    }
}

fun Transformations.setLowerBoundWhen(lowerBound: Int, predicate: (KoreTypedElement) -> Boolean) {
    add(SetLowerBoundWhen(lowerBound, predicate))
}
