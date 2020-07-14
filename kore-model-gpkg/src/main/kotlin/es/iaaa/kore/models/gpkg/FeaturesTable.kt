/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

/**
 * A representation of the model object vector Feature Table.
 * Vector feature data represents geolocated entities including conceptual ones such as districts,
 * real world objects such as roads and rivers, and observations thereof.
 */
object FeaturesTable : KoreClass by koreClass({
    name = "Feature"
    attribute { name = "tableName" }
    attribute { name = "identifier" }
    attribute { name = "description" }
    attribute { name = "maxX" }
    attribute { name = "maxY" }
    attribute { name = "minX" }
    attribute { name = "minY" }
    attribute { name = "srsId" }
})

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.features(features: String, init: KoreClass.() -> Unit) = koreClass(FeaturesTable) {
    name = features
    container = this@features
    init()
    verify(name == features) { "The name property has muted within the block" }
    verify(container == this@features) { "The container property has muted within the block" }
    verify(!tableName.isNullOrBlank()) { "The table name property must not be blank" }
}
