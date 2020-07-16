/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.rules

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.KoreTypedElement
import es.iaaa.kore.copy
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.Transformation
import es.iaaa.kore.transform.Transformations
import mu.KotlinLogging

internal class FlattenTypes(
    val predicate: (KoreNamedElement) -> Boolean,
    val postFlatten: (KoreTypedElement, KoreTypedElement) -> Unit,
    val debugPredicate: (KoreNamedElement) -> String,
    private val global: Boolean,
    private val maxIterations: Int = 4
) : Transformation {

    override fun process(target: Model) {
        var iterations = 0
        do {
            var changes = 0
            val content = if (global) target.allContent() else target.allRelevantContent()
            content.filterIsInstance<KoreClass>().forEach { cls ->
                val toFlatten = cls.attributes.filter { ref ->
                    with(ref) {
                        val withinLimits = upperBound == 1
                        val typeFulfillsPredicate = type?.let { type ->
                            val msg = debugPredicate(type)
                            if (msg.isNotEmpty()) {
                                logger.debug { "Debug predicate:\n$msg" }
                            }
                            predicate(type)
                        } == true
                        val typeAttributesMultiplicity =
                            (type as? KoreClass)?.allAttributes()?.all { att -> att.upperBound == 1 } == true

                        if (withinLimits && typeFulfillsPredicate && !typeAttributesMultiplicity) {
                            logger.debug { "Debug predicate:\nisClass=${(type as? KoreClass) != null}" }

                            (type as? KoreClass)?.allAttributes()?.filter { att -> att.upperBound != 1 }
                                ?.joinToString { "${it.name}:${it.upperBound}" }
                                ?.let {
                                    logger.debug { "Debug predicate:\noffendingAttributes=$it" }
                                }
                        }
                        withinLimits && typeFulfillsPredicate && typeAttributesMultiplicity
                    }
                }
                if (toFlatten.isNotEmpty()) {
                    cls.attributes.also {
                        it.forEach { att -> att.containingClass = null }
                        it.forEach { att ->
                            if (att in toFlatten) {
                                val attributesToBeCopied = (att.type as KoreClass).allAttributes()
                                attributesToBeCopied.forEach { attSrc ->
                                    val attNew = attSrc.copy(cls)
                                    postFlatten(att, attNew)
                                }
                                changes++
                            } else {
                                att.containingClass = cls
                            }
                        }
                    }
                }
            }
            iterations++
        } while (changes > 0 && iterations < maxIterations)
    }
}

fun Transformations.flattenTypes(
    predicate: (KoreNamedElement) -> Boolean,
    postFlatten: (KoreTypedElement, KoreTypedElement) -> Unit,
    debugPredicate: (KoreNamedElement) -> String = { "" },
    global: Boolean = false
) {
    add(FlattenTypes(predicate, postFlatten, debugPredicate, global))
}

private val logger = KotlinLogging.logger {}
