/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.impl

import es.iaaa.kore.transform.Output
import es.iaaa.kore.transform.OutputWriter

class OutputImpl : Output {
    override fun add(writer: OutputWriter) {
        writers.add(writer)
    }

    override var writers: MutableList<OutputWriter> = mutableListOf()
}
