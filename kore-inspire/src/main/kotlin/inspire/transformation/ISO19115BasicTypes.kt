/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.models.gpkg.TextType
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.mapEntry

/**
 * All ISO 19115 property types are transformed to the simple types that GeoPackage knows about.
 */
val `ISO 19115 - Basic types`: Transform = { _, _ ->
    mapEntry(type = "URL", targetType = TextType())
}