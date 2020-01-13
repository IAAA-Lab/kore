@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.GeometryType
import es.iaaa.kore.models.gpkg.PrimaryKey
import es.iaaa.kore.models.gpkg.geoPackageSpec
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Move voidable geometries in feature types with multiple geometries to related feature types`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { references(Stereotypes.featureType) }) {
        val allGeometries = attributes
            .filter { attribute ->
                GeometryType.isInstance(attribute.type) &&
                        attribute.upperBound == 1
            }
        val voidableGeometries = allGeometries.filter { it.references(Stereotypes.voidable) }
        if (allGeometries.size - voidableGeometries.size == 1) {
            val sourceName = name
            val sourceType = this
            val sourceContainer = container
            voidableGeometries.forEach { geometry ->
                koreClass {
                    name = "${sourceName}_${geometry.name}"
                    container = sourceContainer
                    addStereotype(Stereotypes.featureType)
                    koreReference {
                        name = "id"
                        type = sourceType
                        lowerBound = 1
                        upperBound = 1
                        geoPackageSpec().add(PrimaryKey)
                    }
                    geometry.containingClass = this
                    geometry.removeReference(Stereotypes.voidable)
                }
            }
        }
    }
}
