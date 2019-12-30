@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.setTypeWhen

/**
 * All ISO 19115 property types are transformed to the simple types that GeoPackage knows about.
 */
val `ISO 19115 - Basic types`: Transform = { _, _ ->
    setTypeWhen(TextType(), predicate = { it.type?.name == "URL" })
}