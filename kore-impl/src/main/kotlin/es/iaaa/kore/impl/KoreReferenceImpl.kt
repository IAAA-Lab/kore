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

import es.iaaa.kore.KoreReference
import kotlin.properties.Delegates

/**
 * A representation of the model object **Attribute**.
 */
internal open class KoreReferenceImpl : KoreStructuralFeatureImpl(), KoreReference {
    override var isContainement: Boolean by Delegates.vetoable(false) { _, _, newValue ->
        newValue && isContainer
    }
    override val isContainer: Boolean get() = opposite?.isContainement == true
    override var isNavigable: Boolean = false
    override var opposite: KoreReference? = null
    override fun toString(): String = "reference ${containingClass?.name}#$name ${if (isNavigable) "-->" else "<--"} ${opposite?.containingClass?.name}#${opposite?.name}"
}
