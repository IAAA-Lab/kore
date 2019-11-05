/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    override fun findEnumLiteral(name: String): KoreEnumLiteral? = literals.find{ it.name == name }
    override fun findEnumLiteral(value: Int): KoreEnumLiteral? = literals.find{ it.value == value }
    override fun findEnumLiteralByLiteral(literal: String): KoreEnumLiteral? = literals.find{ it.literal == literal }
    internal val internalLiterals: MutableList<KoreEnumLiteral> = mutableListOf()
}
