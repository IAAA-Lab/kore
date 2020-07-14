/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.impl

import es.iaaa.kore.impl.ViolationLevel
import es.iaaa.kore.impl.Violations
import es.iaaa.kore.impl.singleViolation
import es.iaaa.kore.transform.OutputWriter

/**
 * Null writer, useful for testing.
 */
object NullWriter : OutputWriter {
    override fun close() {}

    override fun validate(): Violations =
        singleViolation("Null output writer", level = ViolationLevel.WARNING)
}
