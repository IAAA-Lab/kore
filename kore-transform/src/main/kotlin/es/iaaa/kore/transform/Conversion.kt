/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform

import es.iaaa.kore.KoreObject
import es.iaaa.kore.impl.Validable
import es.iaaa.kore.impl.Violations
import es.iaaa.kore.transform.impl.InputImpl
import es.iaaa.kore.transform.impl.OutputImpl
import es.iaaa.kore.transform.impl.StringConsole
import es.iaaa.kore.transform.impl.TransformationsImpl

/**
 * Conversion
 */
class Conversion : Validable {
    val context: MutableMap<String, Any> = mutableMapOf()
    val input: Input = InputImpl()
    val output: Output = OutputImpl()
    val transformations: Transformations = TransformationsImpl(parent = this)

    lateinit var model: Model

    val lastDryRunOutput: StringBuffer = StringBuffer()

    fun track(obj: KoreObject) {
        model.tracked.add(obj)
    }

    /**
     * Convert a source into multiples targets.
     */
    fun convert(dryRun: Boolean = false) {
        model = Model(input)
        model.load()
        transformations.forEach { it.process(model) }
        if (dryRun) {
            lastDryRunOutput.setLength(0)
            StringConsole(lastDryRunOutput).write(model, context)
        } else {
            output.writers.forEach { writer -> writer.write(model, context) }
        }
    }

    /**
     * Checks if the configuration is well configured and the results of the last run.
     */
    override fun validate(): Violations =
        Violations(listOf(input, output).flatMap { it.validate().violations })

    fun input(init: Input.() -> Unit) {
        input.apply(init)
    }

    fun output(init: Output.() -> Unit) {
        output.apply(init)
    }

    fun transformation(init: Transformations.() -> Unit) {
        transformations.apply(init)
    }
}

fun conversion(init: Conversion.() -> Unit): Conversion {
    return Conversion().apply(init)
}
