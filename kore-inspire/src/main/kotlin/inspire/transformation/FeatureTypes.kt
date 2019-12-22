@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreObject
import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.models.gpkg.FeaturesTable
import es.iaaa.kore.models.gpkg.GeometryType
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.setMetMetaclassWhen

val `simple Feature Type stereotype to GeoPackage Feature`: Transform = { _, _ ->
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature("featureType"))
}

val `Feature Type stereotype without geometry to GeoPackage Attribute`: Transform = { _, _ ->
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute("featureType"))
}

/**
 * Conversion rules of feature types.
 */
val `Feature types`: List<Transform> = listOf(
    `simple Feature Type stereotype to GeoPackage Feature`,
    `Feature Type stereotype without geometry to GeoPackage Attribute`
)

fun canToFeature(name: String): (KoreObject) -> Boolean = {
    when {
        it.references(name) -> (it as? KoreClass)
            ?.attributes
            ?.filter { att -> GeometryType.isInstance(att.type) }
            ?.run { size == 1 && all { att -> att.upperBound == 1 } }
            ?: throw Exception("Not expected: if the instance has the refinement $name it must be a class")
        else -> false
    }
}

fun canToAttribute(name: String): (KoreObject) -> Boolean = {
    when {
        it.references(name) ->
            (it as? KoreClass)
                ?.attributes
                ?.none { att -> GeometryType.isInstance(att.type) }
                ?: throw Exception("Not expected: if the instance has the refinement $name it must be a class")
        else -> false
    }
}
