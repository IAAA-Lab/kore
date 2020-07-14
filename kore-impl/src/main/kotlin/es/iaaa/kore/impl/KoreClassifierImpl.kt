/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KorePackage
import kotlin.reflect.KClass

/**
 * A representation of the model object **Classifier**.
 */
open class KoreClassifierImpl : KoreNamedElementImpl(), KoreClassifier {

    /**
     * It represents the actual Kotlin instance class that this meta object represents.
     */
    override var instanceClass: KClass<out Any>?
        get() = when (instanceClassName) {
            "boolean" -> Boolean::class
            else -> runCatching {
                Class.forName(instanceClassName).kotlin
            }.getOrNull()
        }
        set(value) {
            instanceClassName = value?.java?.name
        }

    /**
     * It represents the name of the Kotlin instance class that this meta object represents.
     */
    override var instanceClassName: String? = null

    /**
     * Returns whether the object is an instance of this classifier.
     */
    override fun isInstance(obj: Any?): Boolean =
        if (instanceClass?.isInstance(obj) == true) true else super.isInstance(obj)

    override var container: KorePackage? = null
        set(value) {
            if (value != container) {
                container?.remove(KorePackage::classifiers.name, this)
                value?.add(KorePackage::classifiers.name, this)
                field = value
            }
        }
}

object KoreRoot : KoreClassifierImpl()