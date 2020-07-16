/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.rules

import es.iaaa.kore.KoreAnnotation
import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class RemoveTags(
    private val tags: List<String>,
    val source: String?
) : Transformation {

    override fun process(target: Model) {
        val matchers = tags.map { it.toRegex() }
        target.allRelevantContent().forEach { obj ->
            if (obj is KoreModelElement) {
                obj.getAnnotation(source)?.let(removeTag(matchers))
            }
        }
    }

    private fun removeTag(matchers: List<Regex>): (KoreAnnotation) -> Unit = { ann ->
        ann.details.keys
            .filter { key -> matchers.any { it.matches(key) } }
            .forEach { key -> ann.details.remove(key) }
    }
}

fun Transformations.removeTags(tags: List<String>, source: String? = null) {
    add(RemoveTags(tags, source))
}
