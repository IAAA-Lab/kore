/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

object Metadata : KoreClass by koreClass({
    name = "Metadata"
    attribute { name = "scope" }
    attribute { name = "standardUri" }
    attribute { name = "mimeType" }
    attribute { name = "metadata" }
})

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.metadata(init: KoreClass.() -> Unit) = koreClass(Metadata) {
    container = this@metadata
    init()
    verify(container == this@metadata) { "The container property has muted within the block" }
}

