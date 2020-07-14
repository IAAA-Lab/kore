/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreAnnotation
import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.KoreObject

/**
 * A representation of the model object **Model Element**.
 */
abstract class KoreModelElementImpl : KoreObjectImpl(), KoreModelElement {

    override val annotations: MutableList<KoreAnnotation> = mutableListOf()

    override fun getAnnotation(source: String?): KoreAnnotation? = annotations.find { it.source == source }

    override val contents: List<KoreObject> get() = annotations

    override fun findOrCreateAnnotation(source: String?): KoreAnnotation =
        getAnnotation(source) ?: KoreAnnotationImpl().also {
            it.source = source
            it.modelElement = this
        }
}


