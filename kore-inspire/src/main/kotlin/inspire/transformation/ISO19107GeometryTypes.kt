@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

/**
 * ISO 19107 defines a set of Geometry types, which need to be mapped to the types available in GeoPackage.
 */
val `ISO 19107 - Geometry types`: Transform = { _, _ ->

    val standardMappings = mapOf(
        "GM_Point" to PointType(),
        "GM_MultiPoint" to MultiPointType(),
        "GM_Curve" to CurveType(),
        "GM_MultiCurve" to MultiCurveType(),
        "GM_MultiSurface" to MultiSurfaceType(),
        "GM_Object" to GeometryType(),
        "GM_Polygon" to PolygonType(),
        "GM_Surface" to SurfaceType()
    )

    val nonStandardMappings = mapOf(
        "GM_Solid" to BlobType(),
        "GM_Tin" to BlobType(),
        "GM_Boundary" to PolygonType(),
        "GM_Primitive" to GeometryType()
    )

    patch<KoreTypedElement>(predicate = { standardMappings.contains(type?.name) }, global = true) {
        type = standardMappings[type?.name]
    }

    patch<KoreTypedElement>(predicate = { nonStandardMappings.contains(type?.name)}, global = true) {
        if ("GM_Solid" == type?.name) {
            (container as KoreClass).attribute {
                name = this@patch.name + "_content_type"
                type = TextType()
                lowerBound = this@patch.lowerBound
                upperBound = this@patch.upperBound
            }
        }
        type = nonStandardMappings[type?.name]
    }
}

