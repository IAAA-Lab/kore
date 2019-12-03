/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.iaaa.kore.transform

import es.iaaa.kore.KoreObject
import es.iaaa.kore.impl.Validable
import es.iaaa.kore.impl.Violations
import es.iaaa.kore.transform.impl.StringConsole
import es.iaaa.kore.transform.impl.InputImpl
import es.iaaa.kore.transform.impl.OutputImpl
import es.iaaa.kore.transform.impl.TransformationsImpl

/**
 * Conversion
 */
class Conversion: Validable {
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
            StringConsole(lastDryRunOutput).write(model)
        } else {
            output.writers.forEach { writer -> writer.write(model) }
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
