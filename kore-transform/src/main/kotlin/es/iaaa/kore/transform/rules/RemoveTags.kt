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

import es.iaaa.kore.KoreAnnotation
import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations

internal class RemoveTags(
    val tags: List<String>,
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
