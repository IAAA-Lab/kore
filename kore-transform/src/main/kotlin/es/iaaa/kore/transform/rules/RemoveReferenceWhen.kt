/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.rules

import com.andreapivetta.kolor.red
import es.iaaa.kore.KoreReference
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations
import es.iaaa.kore.util.toPrettyString

internal class RemoveReferenceWhen(
    val predicate: (KoreReference) -> Boolean
) : Transformation {

    override fun process(target: Model) {
        target.allRelevantContent().filterIsInstance<KoreReference>().filter(predicate).forEach {
            println("${it.containingClass?.name} ${it.toPrettyString()}".red())
            it.containingClass = null
        }
    }
}

fun Transformations.removeReferenceWhen(predicate: (KoreReference) -> Boolean) {
    add(RemoveReferenceWhen(predicate))
}
