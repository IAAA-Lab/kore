/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * A representation of a structural feature
 */
interface KoreStructuralFeature : KoreTypedElement {

    /**
     * Returns an instance of the containing class.
     */
    var containingClass: KoreClass?

    /**
     * True is this feature is changeable (read-write) or not (only read). True by default.
     */
    var isChangeable: Boolean

    /**
     * A serialized form of the [defaultValue]
     */
    var defaultValueLiteral: String?

    /**
     * Represents the default value that the feature must take on when an explicit value has not been set.
     */
    var defaultValue: Any?

    /**
     * True is this feature cannot be unset (i.e. is safe). That is, it may be not initialised, never returns a null
     * value and never accepts a null value. False by default.
     */
    var isUnsettable: Boolean
}