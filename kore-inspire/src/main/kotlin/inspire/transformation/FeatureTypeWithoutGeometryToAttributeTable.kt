@file:Suppress("ObjectPropertyName")
package inspire.transformation

import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.setMetMetaclassWhen

val `Feature types with no geometry to attribute table`: Transform = { _, _ ->
    setMetMetaclassWhen(
        AttributesTable, predicate = canToAttribute(
            Stereotypes.featureType
        )
    )
}