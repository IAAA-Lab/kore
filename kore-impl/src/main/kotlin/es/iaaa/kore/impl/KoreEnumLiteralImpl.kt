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
 * A representation of the model object **Enum Literal**.
 */
internal class KoreEnumLiteralImpl : KoreNamedElementImpl(), KoreEnumLiteral {
    override val container: KoreObject? get() = enum
    override var value: Int = 0
    override var enum: KoreEnum? = null
        set(value) {
            if (value != enum) {
                (enum as? KoreEnumImpl)?.internalLiterals?.remove(this)
                (value as? KoreEnumImpl)?.internalLiterals?.add(this)
                field = value
            }
        }
    override var literal: String? = null
        get() = field ?: name
}
