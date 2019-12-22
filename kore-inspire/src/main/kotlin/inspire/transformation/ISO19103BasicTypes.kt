@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.setTypeWhen

/**
 * All ISO 19103 property types are transformed to the simple types that GeoPackage knows about.
 */
val `ISO 19103 - Basic types`: Transform = { _, _ ->
    setTypeWhen(TextType(), predicate = { it.type?.name == "CharacterString" })
    setTypeWhen(BooleanType(), predicate = { it.type?.name == "Boolean" })
    setTypeWhen(IntegerType(), predicate = { it.type?.name == "Integer" })
    setTypeWhen(DoubleType(), predicate = { it.type?.name == "Real" })
    setTypeWhen(TextType(), predicate = { it.type?.name == "Number" })
    setTypeWhen(TextType(), predicate = { it.type?.name == "Decimal" })
    setTypeWhen(DateTimeType(), predicate = { it.type?.name == "DateTime" })
    setTypeWhen(DateType(), predicate = { it.type?.name == "Date" })
}