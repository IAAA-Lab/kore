/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform

import es.iaaa.kore.KoreClass

/**
 * A transformation is a set of rules.
 */
interface Transformations : List<Transformation> {

    /**
     * Add a rule.
     */
    fun add(transformation: Transformation)

    fun track(list: List<KoreClass>)

    fun owner(): Conversion
}

typealias Transform = Transformations.(Conversion, Map<String, Any>) -> Unit

fun Transformations.manipulation(block: Transform, options: Map<String, Any> = emptyMap()) {
    block(this, owner(), options)
}

fun Transformations.rule(block: Transform, options: Map<String, Any> = emptyMap()) {
    block(this, owner(), options)
}

fun Transformations.rule(blocks: List<Transform>, options: Map<String, Any> = emptyMap()) {
    blocks.forEach { it(this, owner(), options) }
}

fun Transformations.manipulation(blocks: List<Transform>, options: Map<String, Any> = emptyMap()) {
    blocks.forEach { it(this, owner(), options) }
}
