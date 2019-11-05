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
