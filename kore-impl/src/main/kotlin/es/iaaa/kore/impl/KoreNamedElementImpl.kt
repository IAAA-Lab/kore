/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreNamedElement

/**
 * A representation of the model object **Named Element**.
 */
abstract class KoreNamedElementImpl : KoreModelElementImpl(), KoreNamedElement {
    override var name: String? = null
    override val fullName: String?
        get() {
            val parentName = (container as? KoreNamedElement)?.fullName
            return parentName?.let { "$it :: $name" } ?: name
        }
}
