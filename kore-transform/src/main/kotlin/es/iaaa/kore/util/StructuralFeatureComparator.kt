/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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
