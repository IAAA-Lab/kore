/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * An annotation.
 */
interface KoreAnnotation : KoreModelElement {

    /**
     * A URI representing the type of the annotation
     */
    var source: String?

    /**
     * The details map.
     */
    val details: MutableMap<String, String>

    /**
     * The container reference.
     */
    var modelElement: KoreModelElement?

    /**
     * Arbitrary referenced objects
     */
    val references: MutableList<KoreObject>
}
