/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * A typed element.
 */
interface KoreTypedElement : KoreNamedElement {

    /**
     * The type.
     */
    var type: KoreClassifier?

    /**
     * The lower bound.
     */
    var lowerBound: Int

    /**
     * The upper bound.
     */
    var upperBound: Int

    /**
     * Is many?
     */
    val isMany: Boolean get() = upperBound > 1 || unbounded

    /**
     * Is ordered?
     */
    var ordered: Boolean

    /**
     * Is required?
     */
    val required: Boolean get() = lowerBound >= 1

    /**
     * Is unbounded?
     */
    val unbounded: Boolean get() = upperBound == UNBOUNDED_MULTIPLICITY

    companion object {

        /**
         * A value indicating that there is no upper bound.
         */
        const val UNBOUNDED_MULTIPLICITY = -1

        /**
         * A value indicating that there is an unspecified upper bound.
         */
        const val UNSPECIFIED_MULTIPLICITY = -2
    }
}