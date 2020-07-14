/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * A representation of the literal of an enumeration.
 */
interface KoreEnumLiteral : KoreNamedElement {

    /**
     * The int value of an enumerator.
     */
    var value: Int

    /**
     * The contain enumeration
     */
    var enum: KoreEnum?

    /**
     * The string value that represents the value. If set to null, it will return the [name].
     */
    var literal: String?
}