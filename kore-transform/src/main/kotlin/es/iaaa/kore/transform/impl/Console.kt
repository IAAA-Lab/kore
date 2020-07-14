/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.impl

import com.andreapivetta.kolor.yellow
import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreDataType
import es.iaaa.kore.impl.Violations
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.OutputWriter
import es.iaaa.kore.util.toPrettyString

object Console : OutputWriter {
    override fun write(resource: Model, context: Map<String, Any>) {
        println("Selected Packages:".yellow())
        resource.selectedPackages().forEach {
            println(it.toPrettyString())
        }
        println("Dependencies:".yellow())
        resource.allDependencies().forEach {
            when (it) {
                is KoreClass -> println(it.toPrettyString())
                is KoreDataType -> println(it.toPrettyString())
            }
        }
        println("Tracked elements:".yellow())
        resource.tracked.forEach {
            when (it) {
                is KoreClass -> println(it.toPrettyString())
                is KoreDataType -> println(it.toPrettyString())
            }
        }
    }

    override fun validate(): Violations = Violations()
}
