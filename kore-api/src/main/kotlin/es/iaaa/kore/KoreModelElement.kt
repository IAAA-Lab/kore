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
 * A model element.
 * Provides support to annotations.
 */
interface KoreModelElement : KoreObject {

    /**
     * Return the list of annotations.
     */
    val annotations: List<KoreAnnotation>

    /**
     * Return the annotation matching the [source] attribute
     */
    fun getAnnotation(source: String? = null): KoreAnnotation?

    /**
     * Return the annotation matching the [source] attribute or creates it.
     */
    fun findOrCreateAnnotation(source: String? = null): KoreAnnotation
}

/**
 * Return an immutable list with the named [KoreAnnotation.references] of this object.
 */
fun KoreModelElement.findDefaultNamedReferences(): List<KoreNamedElement> =
    getAnnotation()?.references?.filterIsInstance<KoreNamedElement>() ?: emptyList()

/**
 * Find a tagged value.
 */
fun KoreModelElement.findTaggedValue(name: String, source: String? = null): String? =
    getAnnotation(source)?.details?.get(name)