/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreObject
import es.iaaa.kore.KoreOperation
import es.iaaa.kore.KoreParameter

/**
 * A representation of the model object **Parameter**.
 */
internal class KoreParameterImpl : KoreTypedElementImpl(), KoreParameter {
    override val container: KoreObject? get() = operation
    override var operation: KoreOperation? = null
        set(value) {
            if (value != operation) {
                (operation as? KoreOperationImpl)?.internalParameters?.remove(this)
                (value as? KoreOperationImpl)?.internalParameters?.add(this)
                field = value
            }
        }
}
