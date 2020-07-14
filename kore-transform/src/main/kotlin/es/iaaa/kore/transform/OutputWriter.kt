/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform

import es.iaaa.kore.impl.Validable
import java.io.Closeable

/**
 * Output writer interface.
 */
interface OutputWriter : Validable, Closeable {

    /**
     * Writes a resource.
     */
    fun write(resource: Model, context: Map<String, Any>) {}

    override fun close() {}
}
