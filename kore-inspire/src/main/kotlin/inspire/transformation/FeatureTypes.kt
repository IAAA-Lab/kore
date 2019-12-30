@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.models.gpkg.FeaturesTable
import es.iaaa.kore.models.gpkg.GeometryType
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.setMetMetaclassWhen

val `simple Feature Type stereotype to GeoPackage Feature`: Transform = { _, _ ->
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature(Stereotypes.featureType))
}

val `Feature Type stereotype without geometry to GeoPackage Attribute`: Transform = { _, _ ->
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute(Stereotypes.featureType))
}

/**
 * Conversion rules of feature types.
 */
val `Feature types`: List<Transform> = listOf(
    `simple Feature Type stereotype to GeoPackage Feature`,
    `Feature Type stereotype without geometry to GeoPackage Attribute`
)

/**
 * The class can to be transformed to a feature table if it is concrete and has one attribute with a geometry type
 * and a maximum multiplicity of 1.
 */
fun canToFeature(name: String): (KoreObject) -> Boolean = {
    if (it.references(name) && it is KoreClass) {
        !it.isAbstract && it.attributes
            .filter { att -> GeometryType.isInstance(att.type) || GeometryType == att.type?.metaClass }
            .run { size == 1 && all { att -> att.upperBound == 1 } }
    } else {
        false
    }
}

/**
 * The class can to be transformed to an attribute table if it is concrete and no geometry attributes.
 */
fun canToAttribute(name: String): (KoreObject) -> Boolean = {
    if (it.references(name) && it is KoreClass) {
        !it.isAbstract && it.attributes.none { att -> GeometryType.isInstance(att.type) || GeometryType == att.type?.metaClass }
    } else {
        false
    }
}
