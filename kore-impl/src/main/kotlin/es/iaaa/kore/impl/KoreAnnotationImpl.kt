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
 * A representation of the model object **Annotation**.
 */
internal class KoreAnnotationImpl : KoreModelElementImpl(), KoreAnnotation {
    override val container: KoreObject? get() = modelElement
    override var source: String? = null
    override val details: MutableMap<String, String> = mutableMapOf()
    override var modelElement: KoreModelElement? = null
        set(value) {
            if (value != modelElement) {
                (modelElement as? KoreModelElementImpl)?.annotations?.remove(this)
                (value as? KoreModelElementImpl)?.annotations?.add(this)
                field = value
            }
        }
    override val references: MutableList<KoreObject> = mutableListOf()
}