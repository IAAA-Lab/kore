/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreDataType

/**
 * A representation of the model object **Data Type**.
 */
internal open class KoreDataTypeImpl : KoreClassifierImpl(), KoreDataType {
    override fun toString(): String = "type ${container?.name}#$name"
}
