/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * A representation of a reference.
 * TODO: The type of a reference must be a KoreClass.
 */
interface KoreReference : KoreStructuralFeature {

    /**
     * True is this is a containment.
     */
    var isContainement: Boolean

    /**
     * A reference is a container if it has an [opposite] that is a containment.
     */
    val isContainer: Boolean

    /**
     * True if the reference is navigable.
     */
    var isNavigable: Boolean

    /**
     * It represent the other end of a bidirectional relation.
     */
    var opposite: KoreReference?
}