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
import es.iaaa.kore.KorePackage
import es.iaaa.kore.impl.KoreClassImpl
import es.iaaa.kore.koreClass

/**
 * A representation of the model object vector Feature Table.
 * Vector feature data represents geolocated entities including conceptual ones such as districts,
 * real world objects such as roads and rivers, and observations thereof.
 */
object FeaturesTable : KoreClassImpl() {
    init {
        name = "Feature"
        attribute { name = "tableName" }
        attribute { name = "identifier" }
        attribute { name = "description" }
        attribute { name = "maxX" }
        attribute { name = "maxY" }
        attribute { name = "minX" }
        attribute { name = "minY" }
        attribute { name = "srsId" }
    }

    operator fun invoke(init: KoreClass.() -> Unit): KoreClass = koreClass {
        metaClass = FeaturesTable
        description = ""
        init()
    }
}

/**
 * A short hand factory function.
 */
fun feature(tableName: String, init: KoreClass.() -> Unit) = FeaturesTable(init).also {
    it.name = tableName
    it.tableName = tableName
}

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.feature(tableName: String, init: KoreClass.() -> Unit) = FeaturesTable(init).also {
    it.name = tableName
    it.tableName = tableName
    it.container = this
}
