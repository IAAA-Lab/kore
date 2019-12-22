@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.models.gpkg.CurveType
import es.iaaa.kore.models.gpkg.GeometryType
import es.iaaa.kore.models.gpkg.MultiSurfaceType
import es.iaaa.kore.models.gpkg.PointType
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.setTypeWhen

/**
 * ISO 19107 defines a set of Geometry types, which need to be mapped to the types available in GeoPackage.
 */
val `ISO 19107 - Geometry types`: Transform = { _, _ ->
    setTypeWhen(GeometryType(), predicate = { it.type?.name == "GM_Object" })
    setTypeWhen(CurveType(), predicate = { it.type?.name == "GM_Curve" })
    setTypeWhen(MultiSurfaceType(), predicate = { it.type?.name == "GM_MultiSurface" })
    setTypeWhen(PointType(), predicate = { it.type?.name == "GM_Point" })
}