/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
