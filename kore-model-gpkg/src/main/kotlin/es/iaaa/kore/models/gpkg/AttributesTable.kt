/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

/**
 * A representation of the model object Attribute Table.
 * Non-spatial attribute data are sets (or tuples or rows) of observations that may not have an explicit geometry
 * property.
 */
object AttributesTable : KoreClass by koreClass({
    name = "Attributes"
    attribute { name = "tableName" }
    attribute { name = "description" }
    attribute { name = "identifier" }
})

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.attributes(attributes: String, init: KoreClass.() -> Unit) = koreClass(AttributesTable) {
    name = attributes
    container = this@attributes
    init()
    verify(name == attributes) { "The name property has muted within the block" }
    verify(container == this@attributes) { "The container property has muted within the block" }
    verify(!tableName.isNullOrBlank()) { "The table name property must not be blank" }
}
