/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreEnum
import es.iaaa.kore.KoreEnumLiteral
import es.iaaa.kore.KoreObject

/**
 * A representation of the model object **Enum**.
 */
internal class KoreEnumImpl : KoreDataTypeImpl(), KoreEnum {
    override val literals: List<KoreEnumLiteral> get() = internalLiterals.toList()
    override val contents: List<KoreObject> get() = super.contents + literals
    override fun findEnumLiteral(name: String): KoreEnumLiteral? = literals.find { it.name == name }
    override fun findEnumLiteral(value: Int): KoreEnumLiteral? = literals.find { it.value == value }
    override fun findEnumLiteralByLiteral(literal: String): KoreEnumLiteral? = literals.find { it.literal == literal }
    internal val internalLiterals: MutableList<KoreEnumLiteral> = mutableListOf()
}
