/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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
        from.filterIsInstance(klass).asSequence().filter { predicate(it) }.forEach { block(it) }
    }
}

inline fun <reified T> Transformations.patch(
    noinline predicate: T.() -> Boolean = { true },
    global: Boolean = false,
    noinline block: T.() -> Unit
) {
    add(Patch(T::class.java, predicate, global, block))
}
