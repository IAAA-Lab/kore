/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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
