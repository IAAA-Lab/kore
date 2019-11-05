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
import es.iaaa.kore.KoreOperation
import es.iaaa.kore.KoreParameter

/**
 * A representation of the model object **Operation**.
 */
internal class KoreOperationImpl : KoreTypedElementImpl(), KoreOperation {
    override val container: KoreObject? get() = containingClass
    override var containingClass: KoreClass? = null
        set(value) {
            if (value != containingClass) {
                (containingClass as KoreClassImpl?)?.internalOperations?.remove(this)
                (value as KoreClassImpl?)?.internalOperations?.add(this)
                field = value
            }
        }
    override val parameters: List<KoreParameter> get() = internalParameters.toList()

    internal val internalParameters: MutableList<KoreParameter> = mutableListOf()

    override fun isOverrideOf(someOperation: KoreOperation): Boolean {
        if (someOperation.containingClass?.isSuperTypeOf(containingClass) == true && someOperation.name == name) {
            val otherParameters = someOperation.parameters
            if (parameters.size == otherParameters.size) {
                parameters.zip(otherParameters).all { (a, b) -> a.type == b.type }
                return true
            }
        }
        return false
    }

    override val contents: List<KoreObject> get() = super.contents + parameters
}
