/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.copy
import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.models.gpkg.FeaturesTable
import es.iaaa.kore.toReference
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Convert potential references`: Transform = { conversion, _ ->

    patch<KoreClass>(predicate = { metaClass in listOf(AttributesTable, FeaturesTable) }) {
        allAttributes().filter { it.containingClass != this }.forEach { it.copy(this) }
        allReferences().filter { it.containingClass != this }.forEach { it.copy(this) }
    }
    patch<KoreAttribute>(predicate = { type?.metaClass == AttributesTable }) {
        toReference()
    }
    patch<KoreClass>(predicate = { attributes.any { it.upperBound != 1 } }) {
        attributes.filter { it.upperBound != 1 }.forEach { it.toReference() }
    }
}

