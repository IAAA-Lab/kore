@file:Suppress("ObjectPropertyName")


package inspire.transformation

import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.models.gpkg.FeaturesTable
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.setMetMetaclassWhen

val `simple feature-like Data Type stereotype to GeoPackage Feature`: Transform = { _, _ ->
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature("dataType"))
}

val `Data Type stereotype to GeoPackage Attribute`: Transform = { _, _ ->
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute("dataType"))
}


/**
 * Conversion rules of data types.
 */
val `Data types`: List<Transform> = listOf(
    `Data Type stereotype to GeoPackage Attribute`,
    `simple feature-like Data Type stereotype to GeoPackage Feature`
)