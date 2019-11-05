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