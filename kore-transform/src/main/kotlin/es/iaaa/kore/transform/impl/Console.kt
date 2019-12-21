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
