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
