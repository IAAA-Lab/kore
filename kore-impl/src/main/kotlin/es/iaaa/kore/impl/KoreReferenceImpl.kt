/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreReference
import kotlin.properties.Delegates

/**
 * A representation of the model object **Attribute**.
 */
internal open class KoreReferenceImpl : KoreStructuralFeatureImpl(), KoreReference {
    override var isContainement: Boolean by Delegates.vetoable(false) { _, _, newValue ->
        newValue && isContainer
    }
    override val isContainer: Boolean get() = opposite?.isContainement == true
    override var isNavigable: Boolean = false
    override var opposite: KoreReference? = null
    override fun toString(): String = "reference ${containingClass?.name}#$name ${if (isNavigable) "-->" else "<--"} ${opposite?.containingClass?.name}#${opposite?.name}"
}
