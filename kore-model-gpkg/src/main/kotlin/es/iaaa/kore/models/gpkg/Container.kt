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

import es.iaaa.kore.KoreModel
import es.iaaa.kore.KorePackage
import es.iaaa.kore.impl.KoreClassImpl
import es.iaaa.kore.impl.KoreStorage

/**
 * A representation of the model object SQLite Container.
 */
object Container : KoreClassImpl() {
    init {
        name = "Container"
        attribute { name = "fileName" }
    }

    operator fun invoke(init: KorePackage.() -> Unit): KorePackage = KoreModel.createPackage().apply {
        metaClass = Container
        nsUri = "http://www.geopackage.org/spec"
        nsPrefix = "gpkg"
        init()
    }
}

/**
 * The location of the container.
 */
var KorePackage.fileName: String? by KoreStorage()

/**
 * A short hand factory function.
 */
fun container(init: KorePackage.() -> Unit) = Container(init)
