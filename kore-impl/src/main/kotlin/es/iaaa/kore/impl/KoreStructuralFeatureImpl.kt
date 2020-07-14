/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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
