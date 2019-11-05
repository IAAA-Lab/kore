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
package es.iaaa.kore.resource

import es.iaaa.kore.resource.impl.EnterpriseArchitectUml13Factory
import java.io.File

/**
 * A registry of resource factories.
 */
object ResourceFactory {
    val factories: MutableMap<String, Factory> = mutableMapOf(
        "EA-UML1.3" to EnterpriseArchitectUml13Factory
    )

    fun createResource(file: File, format: String, alias: Map<String, String>): Resource {
        return factories[format]?.createResource(file, alias) ?: throw IllegalArgumentException("Unknown $format")
    }
}

/**
 * Common interface for factories.
 */
interface Factory {
    fun createResource(file: File, alias: Map<String, String>): Resource
}
