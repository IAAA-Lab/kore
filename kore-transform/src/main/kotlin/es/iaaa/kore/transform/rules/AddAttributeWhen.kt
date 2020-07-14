/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.rules

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class AddAttributeWhen(
    val provider: (KoreClass) -> KoreAttribute,
    val predicate: (KoreClass) -> Boolean
) : Transformation {

    override fun process(target: Model) {
        target.allRelevantContent().filterIsInstance<KoreClass>().filter(predicate)
            .forEach { provider(it) }
    }
}

fun Transformations.addAttributeWhen(provider: (KoreClass) -> KoreAttribute, predicate: (KoreClass) -> Boolean) {
    add(AddAttributeWhen(provider, predicate))
}
