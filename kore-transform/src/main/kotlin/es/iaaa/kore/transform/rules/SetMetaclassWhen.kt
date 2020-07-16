/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.rules

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreObject
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class SetMetaclassWhen(
    val predicate: (KoreObject) -> Boolean,
    val metaClass: KoreClass,
    private val onlyRoots: Boolean
) : Transformation {

    override fun process(target: Model) {
        val from = if (onlyRoots) target.selectedPackages() else target.allRelevantContent()
        from.filter(predicate).forEach {
            it.metaClass = metaClass
        }
    }
}

fun Transformations.setMetMetaclassWhen(
    metaClass: KoreClass,
    onlyRoots: Boolean = false,
    predicate: (KoreObject) -> Boolean
) {
    add(SetMetaclassWhen(predicate, metaClass, onlyRoots))
}
