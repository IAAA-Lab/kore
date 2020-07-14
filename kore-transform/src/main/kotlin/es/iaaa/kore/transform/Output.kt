/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform

import es.iaaa.kore.impl.Validable
import es.iaaa.kore.impl.Violations

/**
 * An output consist of a list of output writers.
 */
interface Output : Validable {

    /**
     * A list of output writers.
     */
    val writers: List<OutputWriter>

    /**
     * Checks if the writers are well configured.
     */
    override fun validate(): Violations =
        Violations(writers.flatMap { it.validate() })

    /**
     * Add an output writer to the configuration.
     */
    fun add(writer: OutputWriter)
}
