@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.mapEntry

/**
 * All ISO 19103 property types are transformed to the simple types that GeoPackage knows about.
 */
val `ISO 19103 - Basic types`: Transform = { _, _ ->
    mapEntry(type = "CharacterString", targetType = TextType())
    mapEntry(type = "URI", targetType = TextType())
    mapEntry(type = "Boolean", targetType = BooleanType())
    mapEntry(type = "Integer", targetType = IntegerType())
    mapEntry(type = "Real", targetType = RealType())
    mapEntry(type = "Decimal", targetType = RealType())
    mapEntry(type = "Number", targetType = RealType())
    mapEntry(type = "Date", targetType = DateType())
    mapEntry(type = "DateTime", targetType = DateTimeType())
}