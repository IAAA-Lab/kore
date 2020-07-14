/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.rules

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class ForEachAttribute(
    val predicate: (KoreAttribute) -> Boolean,
    val process: (KoreAttribute) -> Unit
) : Transformation {

    override fun process(target: Model) {
        val from = target.allRelevantContent()
        from.filterIsInstance<KoreAttribute>().filter(predicate).forEach {
            process(it)
        }
    }
}

fun Transformations.forEachAttribute(
    predicate: (KoreAttribute) -> Boolean,
    process: (KoreAttribute) -> Unit = {}
) {
    add(ForEachAttribute(predicate, process))
}
