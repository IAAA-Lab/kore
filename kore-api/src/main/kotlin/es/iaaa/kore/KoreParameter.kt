/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * The representation of a parameter of a [KoreOperation].
 */
interface KoreParameter : KoreTypedElement {

    /**
     * It represents the containing operation.
     */
    var operation: KoreOperation?
}