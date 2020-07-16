/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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
    add(
        MapEntry(
            targetType,
            type?.let { { e: KoreTypedElement -> e.type?.name == it } } ?: typePredicate,
            preset,
            postset))
}
