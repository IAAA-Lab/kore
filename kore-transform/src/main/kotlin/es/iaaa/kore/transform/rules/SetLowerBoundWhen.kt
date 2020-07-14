/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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
