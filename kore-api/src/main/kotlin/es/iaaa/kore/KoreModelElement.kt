/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * A model element.
 * Provides support to annotations.
 */
interface KoreModelElement : KoreObject {

    /**
     * Return the list of annotations.
     */
    val annotations: List<KoreAnnotation>

    /**
     * Return the annotation matching the [source] attribute
     */
    fun getAnnotation(source: String? = null): KoreAnnotation?

    /**
     * Return the annotation matching the [source] attribute or creates it.
     */
    fun findOrCreateAnnotation(source: String? = null): KoreAnnotation
}

/**
 * Return an immutable list with the named [KoreAnnotation.references] of this object.
 */
fun KoreModelElement.findDefaultNamedReferences(): List<KoreNamedElement> =
    getAnnotation()?.references?.filterIsInstance<KoreNamedElement>() ?: emptyList()

/**
 * Find a tagged value.
 */
fun KoreModelElement.findTaggedValue(name: String, source: String? = null): String? =
    getAnnotation(source)?.details?.get(name)