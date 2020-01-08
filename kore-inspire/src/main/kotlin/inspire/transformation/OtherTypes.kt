@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.mapEntry

/**
 * Other types
 *
 * Aligned to [ShapeChange standard type mapping for GeoPackage](https://github.com/ShapeChange/ShapeChange/blob/master/src/main/resources/config/StandardGeoPackageMapEntries.xml)
 */
val `Other types`: Transform = { _, _ ->
    mapEntry(type = "Short", targetType = IntegerType())
    mapEntry(type = "Long", targetType = IntegerType())
 }