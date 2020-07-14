/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.resource

import es.iaaa.kore.KoreObject

/**
 * A persistent document.
 */
interface Resource {

    /**
     * Returns the list of the direct content objects
     */
    val contents: MutableList<KoreObject>

    /**
     * Returns whether the resource is loaded.
     */
    val isLoaded: Boolean

    /**
     * Returns a list of the errors in the resource.
     */
    val errors: MutableList<String>

    /**
     * Returns a list of the warnings and informational messages in the resource.
     */
    val warnings: MutableList<String>
}

/**
 * Returns a list that iterates over all the direct contents and indirect contents of this resource.
 */
fun Resource.allContents(): Sequence<KoreObject> = sequence {
    yieldAll(contents)
    contents.forEach { yieldAll(it.allContents()) }
}

