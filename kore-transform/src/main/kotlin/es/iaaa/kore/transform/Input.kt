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

import es.iaaa.kore.KorePackage
import es.iaaa.kore.impl.Validable
import es.iaaa.kore.impl.Violations
import es.iaaa.kore.impl.validateFactoryExists
import es.iaaa.kore.impl.validateFileExists
import es.iaaa.kore.impl.validateIsInitialized

/**
 * An input consist of a file, a file type and a package selector.
 */
interface Input : Validable {
    /**
     * The location of the input file.
     */
    val file: Property<String>

    /**
     * The file type.
     */
    val type: Property<String>

    /**
     * The selector.
     */
    val selector: Property<(KorePackage) -> Boolean>

    /**
     * Aliases.
     */
    val alias: Property<Map<String, String>>

    /**
     * Returns a [Violations] object indicating if this input
     * is valid, and if not, provides details of the violations found.
     */
    override fun validate(): Violations = Violations().apply {
        validateIsInitialized(file)
        validateIsInitialized(type)
        validateFileExists(file)
        validateFactoryExists(type)
    }
}
