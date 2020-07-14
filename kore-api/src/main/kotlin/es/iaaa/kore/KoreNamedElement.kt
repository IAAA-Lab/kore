/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * A named element.
 */
interface KoreNamedElement : KoreModelElement {

    /**
     * The name of this element.
     */
    var name: String?

    /**
     * The full name of this element.
     */
    val fullName: String?
}