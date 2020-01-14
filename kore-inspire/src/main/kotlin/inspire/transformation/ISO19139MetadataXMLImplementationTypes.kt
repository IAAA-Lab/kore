@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.models.gpkg.TextType
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.mapEntry

/**
 * URI is encoded as TEXT.
 */
val `ISO 19139 type - URI`: Transform = { _, _ ->
    mapEntry(type = "URI", targetType = TextType())
}

val `ISO 19139 - Metadata XML Implementation Types`: List<Transform> = listOf(
    `ISO 19139 type - URI`
)

