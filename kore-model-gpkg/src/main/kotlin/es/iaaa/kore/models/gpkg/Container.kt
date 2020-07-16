/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

/**
 * A representation of the model object SQLite Container.
 */
object Container : KoreClass by koreClass({
    name = "Container"
    attribute { name = "fileName" }
})

/**
 * A short hand factory function.
 */
fun container(init: KorePackage.() -> Unit) = korePackage(Container) {
    nsUri = "http://www.geopackage.org/spec"
    nsPrefix = "gpkg"
    init()
    verify(nsUri == "http://www.geopackage.org/spec") { "The nsUri property has muted within the block" }
    verify(nsPrefix == "gpkg") { "The nsPrefix property has muted within the block" }
}
