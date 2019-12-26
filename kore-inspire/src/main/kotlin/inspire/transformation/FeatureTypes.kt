@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreNamedElement
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

/**
 * The class can to be transformed to a feature table if it is concrete and has one attribute with a geometry type
 * and a maximum multiplicity of 1.
 */
fun canToFeature(name: String): (KoreObject) -> Boolean = {
    if (it.references(name)) {
        if (it is KoreClass) {
            !it.isAbstract && it.attributes
                .filter { att -> GeometryType.isInstance(att.type) }
                .run { size == 1 && all { att -> att.upperBound == 1 } }
        } else {
            throw Exception("Not expected: if the instance has the refinement $name it must be a class but ${(it as? KoreNamedElement)?.fullName} was ${it.javaClass.simpleName}")
        }
    } else {
        false
    }
}

/**
 * The class can to be transformed to an attribute table if it is concrete and no geometry attributes.
 */
fun canToAttribute(name: String): (KoreObject) -> Boolean = {
    if (it.references(name)) {
        if (it is KoreClass) {
            !it.isAbstract && it.attributes.none { att -> GeometryType.isInstance(att.type) }
        } else {
            throw Exception("Not expected: if the instance has the refinement $name it must be a class but ${(it as? KoreNamedElement)?.fullName} was ${it.javaClass.simpleName}\"")
        }
    } else {
        false
    }
}
