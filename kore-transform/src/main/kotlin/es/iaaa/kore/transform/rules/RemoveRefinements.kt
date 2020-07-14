/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.rules

import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class RemoveRefinements(
    val names: List<String>
) : Transformation {

    override fun process(target: Model) {
        val matchers = names.map { it.toRegex() }
        target.allRelevantContent().forEach { obj ->
            if (obj is KoreModelElement) {
                obj.annotations.forEach { ann ->
                    ann.references.removeIf { refinement ->
                        if (refinement is KoreNamedElement) {
                            refinement.name?.let {
                                matchers.any { matcher -> matcher.matches(it) }
                            } ?: false
                        } else false
                    }
                }
            }
        }
    }
}

fun Transformations.removeRefinements(names: List<String>) {
    add(RemoveRefinements(names))
}
