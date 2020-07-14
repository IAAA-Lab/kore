/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * The enumeration data type
 */
interface KoreEnum : KoreDataType {

    /**
     * Represents the enumerators of the enumeration.
     */
    val literals: List<KoreEnumLiteral>

    /**
     * Return the enum literal by [name].
     */
    fun findEnumLiteral(name: String): KoreEnumLiteral?

    /**
     * Return the enum literal by [value].
     */
    fun findEnumLiteral(value: Int): KoreEnumLiteral?

    /**
     * Return the enum literal by [literal].
     */
    fun findEnumLiteralByLiteral(literal: String): KoreEnumLiteral?
}