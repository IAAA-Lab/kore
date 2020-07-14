/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform

/**
 * A rule applies a transformation into an object.
 */
interface Transformation {

    /**
     * The target of the process.
     */
    fun process(target: Model)
}
