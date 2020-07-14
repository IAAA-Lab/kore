/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KoreTypedElement

/**
 * A representation of the model object **Typed Element**.
 */
internal abstract class KoreTypedElementImpl : KoreNamedElementImpl(), KoreTypedElement {
    override var ordered: Boolean = true
    override var lowerBound: Int = 0
    override var upperBound: Int = 1
    override var type: KoreClassifier? = null
    override val isMany: Boolean get() = upperBound > 1 || unbounded
    override val required: Boolean get() = lowerBound >= 1
    override val unbounded: Boolean get() = upperBound == KoreTypedElement.UNBOUNDED_MULTIPLICITY

    fun String?.parseUpperBound(): Int? = when {
        this == null -> null
        this == "*" -> KoreTypedElement.UNBOUNDED_MULTIPLICITY
        else -> toInt()
    }
}
