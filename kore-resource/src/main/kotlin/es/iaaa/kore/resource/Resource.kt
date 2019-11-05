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

import es.iaaa.kore.KoreObject

/**
 * A persistent document.
 */
interface Resource {

    /**
     * Returns the list of the direct content objects
     */
    val contents: MutableList<KoreObject>

    /**
     * Returns whether the resource is loaded.
     */
    val isLoaded: Boolean

    /**
     * Returns a list of the errors in the resource.
     */
    val errors: MutableList<String>

    /**
     * Returns a list of the warnings and informational messages in the resource.
     */
    val warnings: MutableList<String>
}

/**
 * Returns a list that iterates over all the direct contents and indirect contents of this resource.
 */
fun Resource.allContents(): Sequence<KoreObject> = sequence {
    yieldAll(contents)
    contents.forEach { yieldAll(it.allContents()) }
}

