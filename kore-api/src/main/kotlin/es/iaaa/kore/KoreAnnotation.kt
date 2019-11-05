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
package es.iaaa.kore

/**
 * An annotation.
 */
interface KoreAnnotation : KoreModelElement {

    /**
     * A URI representing the type of the annotation
     */
    var source: String?

    /**
     * The details map.
     */
    val details: MutableMap<String, String>

    /**
     * The container reference.
     */
    var modelElement: KoreModelElement?

    /**
     * Arbitrary referenced objects
     */
    val references: MutableList<KoreObject>
}
