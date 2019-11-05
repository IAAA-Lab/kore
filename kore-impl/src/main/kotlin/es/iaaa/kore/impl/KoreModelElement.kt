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
package es.iaaa.kore.impl

import es.iaaa.kore.KoreAnnotation
import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.KoreObject

/**
 * A representation of the model object **Model Element**.
 */
abstract class KoreModelElementImpl : KoreObjectImpl(), KoreModelElement {

    override val annotations: MutableList<KoreAnnotation> = mutableListOf()

    override fun getAnnotation(source: String?): KoreAnnotation? = annotations.find { it.source == source }

    override val contents: List<KoreObject> get() = annotations

    override fun findOrCreateAnnotation(source: String?): KoreAnnotation =
        getAnnotation(source) ?: KoreAnnotationImpl().also {
            it.source = source
            it.modelElement = this
        }
}


