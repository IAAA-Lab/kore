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

import es.iaaa.kore.KoreStructuralFeature
import es.iaaa.kore.findDefaultNamedReferences

object StructuralFeatureComparator : Comparator<KoreStructuralFeature> {
    override fun compare(o1: KoreStructuralFeature, o2: KoreStructuralFeature): Int {
        val r1 = o1.findDefaultNamedReferences()
        val r2 = o2.findDefaultNamedReferences()
        return when {
            o1.name == null -> 1
            o2.name == null -> -1
            r1.size == r2.size -> compareNames(o1.name, o2.name)
            r1.isEmpty() || r2.isEmpty() -> r1.size - r2.size
            else -> r2.size - r1.size
        }
    }

    fun compareNames(n1: String?, n2: String?): Int =
        when {
            n1 != null && n2 != null -> n1.compareTo(n2)
            n2 == null -> -1
            n1 == null -> 1
            else -> 0
        }
}
