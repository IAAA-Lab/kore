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

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreModel
import es.iaaa.kore.KorePackage

/**
 * A representation of the model object Attribute Table.
 * Non-spatial attribute data are sets (or tuples or rows) of observations that may not have an explicit geometry
 * property.
 */
val AttributesTable = KoreModel.createClass().apply {
    name = "Attributes"
    attribute { name = "tableName" }
    attribute { name = "description" }
    attribute { name = "identifier" }
}

/**
 * A short hand factory function.
 */
fun attributes(tableName: String, init: KoreClass.() -> Unit) = KoreModel.createClass().apply {
    metaClass = AttributesTable
    name = tableName
    this.tableName = tableName
    description = ""
    init()
}

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.attributes(tableName: String, init: KoreClass.() -> Unit) = KoreModel.createClass().apply {
    metaClass = AttributesTable
    name = tableName
    this.tableName = tableName
    description = ""
    container = this@attributes
    init()
}
