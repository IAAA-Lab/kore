/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.impl

import es.iaaa.kore.KoreClass
import es.iaaa.kore.transform.Conversion
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

class TransformationsImpl(
    val transformations: MutableList<Transformation> = mutableListOf(),
    val parent: Conversion
) : Transformations, List<Transformation> by transformations {
    val shouldTrack: MutableList<KoreClass> = mutableListOf()
    override fun add(transformation: Transformation) {
        transformations.add(transformation)
    }

    override fun track(list: List<KoreClass>) {
        shouldTrack.addAll(list)
    }

    override fun owner(): Conversion = parent
}
