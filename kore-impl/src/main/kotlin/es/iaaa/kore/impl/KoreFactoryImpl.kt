/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreFactory
import es.iaaa.kore.KoreObject

internal open class KoreFactoryImpl : KoreModelElementImpl(), KoreFactory {
    override val container: KoreObject? get() = null
}