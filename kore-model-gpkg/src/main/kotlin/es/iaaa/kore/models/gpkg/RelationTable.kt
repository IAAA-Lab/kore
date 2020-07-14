/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

/**
 * A representation of the model object Relation Table.
 * A relation is a mapping between existing table types.
 */
object RelationTable : KoreClass by koreClass({
    name = "Relation"
    attribute { name = "tableName" }
    attribute { name = "profile" }
    attribute { name = "relatedReference" }
    attribute { name = "identifier" }
})

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.relation(relationName: String, init: KoreClass.() -> Unit) = koreClass(RelationTable) {
    name = relationName
    container = this@relation
    init()
    verify(name == relationName) { "The name property has muted within the block" }
    verify(container == this@relation) { "The container property has muted within the block" }
    verify(!tableName.isNullOrBlank()) { "The table name property must not be blank" }
}
