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
 * A representation of the model object **Annotation**.
 */
internal class KoreAnnotationImpl : KoreModelElementImpl(), KoreAnnotation {
    override val container: KoreObject? get() = modelElement
    override var source: String? = null
    override val details: MutableMap<String, String> = mutableMapOf()
    override var modelElement: KoreModelElement? = null
        set(value) {
            if (value != modelElement) {
                (modelElement as? KoreModelElementImpl)?.annotations?.remove(this)
                (value as? KoreModelElementImpl)?.annotations?.add(this)
                field = value
            }
        }
    override val references: MutableList<KoreObject> = mutableListOf()
    override fun toString(): String = "annotation $details"
}