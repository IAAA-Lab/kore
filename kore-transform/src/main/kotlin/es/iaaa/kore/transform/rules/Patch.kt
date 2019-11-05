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
package es.iaaa.kore.transform.rules

import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

class Patch<T>(
    val klass: Class<T>,
    val predicate: T.() -> Boolean = { true },
    val global: Boolean = false,
    val block: T.() -> Unit
) : Transformation {

    override fun process(target: Model) {
        val from = if (global) target.allContent() else target.allRelevantContent()
        from.filterIsInstance(klass).filter { predicate(it) }.forEach { block(it) }
    }
}

inline fun <reified T> Transformations.patch(
    noinline predicate: T.() -> Boolean,
    global: Boolean = false,
    noinline block: T.() -> Unit
) {
    add(Patch(T::class.java, predicate, global, block))
}
