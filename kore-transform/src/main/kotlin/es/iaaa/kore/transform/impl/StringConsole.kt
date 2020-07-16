/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.impl

import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KorePackage
import es.iaaa.kore.impl.Violations
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.OutputWriter
import es.iaaa.kore.util.toPrettyString

class StringConsole(private val out: StringBuffer = StringBuffer()) : OutputWriter {
    override fun write(resource: Model, context: Map<String, Any>) {
        resource.allRelevantContent().mapNotNull {
            when (it) {
                is KorePackage -> it
                is KoreClassifier -> it.container
                else -> null
            }
        }.distinct().forEach {
            out.appendln(it.toPrettyString(resource.allRelevantContent()))
        }
    }

    override fun validate(): Violations = Violations()
}
