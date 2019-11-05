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
import es.iaaa.kore.impl.KoreClassImpl

/**
 * A representation of the model object Relation Table.
 * A relation is a mapping between existing table types.
 */
object RelationTable : KoreClassImpl() {
    init {
        name = "Relation"
        attribute { name = "tableName" }
        attribute { name = "profile" }
        attribute { name = "relatedReference" }
    }

    operator fun invoke(init: KoreClass.() -> Unit): KoreClass = KoreModel.createClass().apply {
        metaClass = RelationTable
        init()
    }
}

/**
 * A short hand factory function.
 */
fun relation(tableName: String, init: KoreClass.() -> Unit) = RelationTable(init).also {
    it.name = tableName
    it.tableName = tableName
}

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.relation(tableName: String, init: KoreClass.() -> Unit) = RelationTable(init).also {
    it.name = tableName
    it.tableName = tableName
    it.container = this
}
