/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

import kotlin.reflect.KClass

/**
 * A classifier describes sets of instances having common features.
 */
interface KoreClassifier : KoreNamedElement {

    /**
     * It represents the actual Kotlin instance class that this meta object represents.
     */
    var instanceClass: KClass<out Any>?

    /**
     * It represents the name of the Kotlin instance class that this meta object represents.
     */
    var instanceClassName: String?

    /**
     * The container is restricted to a [KorePackage].
     */
    override var container: KorePackage?
}