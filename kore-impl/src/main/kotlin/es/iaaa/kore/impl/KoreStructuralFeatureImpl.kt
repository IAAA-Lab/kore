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

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreObject
import es.iaaa.kore.KoreStructuralFeature

/**
 * A representation of the model object **Structural Features**.
 */
internal abstract class KoreStructuralFeatureImpl : KoreTypedElementImpl(), KoreStructuralFeature {
    override var containingClass: KoreClass? = null
        set(value) {
            if (value != containingClass) {
                containingClass?.remove("structuralFeatures", this)
                value?.add("structuralFeatures", this)
                field = value
            }
        }
    override var isChangeable: Boolean = true
    override var defaultValueLiteral: String? = null
    override var defaultValue: Any? = null
    override var isUnsettable: Boolean = false
    override val container: KoreObject? get() = containingClass
    override fun toString(): String = "feature ${containingClass?.name}#$name"
}
