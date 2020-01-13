@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreObject
import es.iaaa.kore.models.gpkg.FeaturesTable
import es.iaaa.kore.models.gpkg.GeometryType
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.setMetMetaclassWhen

val `Feature types with single geometry to feature table`: Transform = { _, _ ->
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature(Stereotypes.featureType))
}

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
