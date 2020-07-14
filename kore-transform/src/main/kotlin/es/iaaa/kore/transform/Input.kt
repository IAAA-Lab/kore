/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform

import es.iaaa.kore.KoreObject
import es.iaaa.kore.KorePackage
import es.iaaa.kore.impl.*
import es.iaaa.kore.resource.ResourceHelper

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
     * The package selector.
     */
    val selector: Property<(KorePackage) -> Boolean>

    /**
     * The helper.
     */
    val helper: Property<ResourceHelper>

    /**
     * The object selector.
     */
    val boundary: Property<(KoreObject, KoreObject) -> Boolean>

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
