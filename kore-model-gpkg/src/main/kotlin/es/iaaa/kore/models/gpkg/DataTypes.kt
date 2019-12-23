/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreDataType
import es.iaaa.kore.KoreModel
import es.iaaa.kore.attribute
import es.iaaa.kore.impl.KoreClassImpl
import es.iaaa.kore.impl.KoreStorage
import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.sf.GeometryType

/**
 * Common metaclass of GeoPackage Data types.
 */
object GpkgDataType : KoreClassImpl() {
    init {
        attribute { name = "ngaGeoPackageDataType" }
        attribute { name = "ngaGeometryType" }
    }
}

/**
 * Reference to the identifier in the NGA implementation.
 */
var KoreDataType.ngaGeoPackageDataType: GeoPackageDataType? by KoreStorage()

fun create(parent: KoreClass, type: GeoPackageDataType): KoreDataType {
    return KoreModel.createDataType().apply {
        metaClass = parent
        name = parent.name
        ngaGeoPackageDataType = type
    }
}

/**
 * Reference to the identifier in the NGA implementation.
 */
var KoreDataType.ngaGeometryType: GeometryType? by KoreStorage()

fun create(parent: KoreClass, type: GeometryType): KoreDataType = KoreModel.createDataType().apply {
    metaClass = parent
    name = parent.name
    ngaGeometryType = type
}

/**
 * A boolean value representing true or false.
 * Stored as SQLite INTEGER with value 0 for false or 1 for true.
 */
object BooleanType : KoreClassImpl() {
    init {
        superTypes.add(IntegerType)
        name = "BOOLEAN"
    }

    operator fun invoke(): KoreDataType = create(BooleanType, GeoPackageDataType.BOOLEAN)
}

/**
 * 8-bit signed two’s complement integer.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object TinyIntType : KoreClassImpl() {
    init {
        superTypes.add(IntegerType)
        name = "TINYINT"
    }

    operator fun invoke(): KoreDataType = create(TinyIntType, GeoPackageDataType.TINYINT)
}

/**
 * 16-bit signed two’s complement integer.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object SmallIntType : KoreClassImpl() {
    init {
        superTypes.add(IntegerType)
        name = "SMALLINT"
    }

    operator fun invoke(): KoreDataType = create(SmallIntType, GeoPackageDataType.SMALLINT)
}

/**
 * 32-bit signed two’s complement integer.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object MediumIntType : KoreClassImpl() {
    init {
        superTypes.add(IntegerType)
        name = "MEDIUMINT"
    }

    operator fun invoke(): KoreDataType = create(MediumIntType, GeoPackageDataType.MEDIUMINT)
}

/**
 * 64-bit signed two’s complement integer.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object IntegerType : KoreClassImpl() {
    init {
        superTypes.add(GpkgDataType)
        name = "INTEGER"
    }

    operator fun invoke(): KoreDataType = create(IntegerType, GeoPackageDataType.INTEGER)
}

/**
 * 32-bit IEEE floating point number.
 */
object FloatType : KoreClassImpl() {
    init {
        superTypes.add(DoubleType)
        name = "FLOAT"
    }

    operator fun invoke(): KoreDataType = create(FloatType, GeoPackageDataType.FLOAT)
}

/**
 * 64-bit IEEE floating point number.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object DoubleType : KoreClassImpl() {
    init {
        superTypes.add(GpkgDataType)
        name = "DOUBLE"
    }

    operator fun invoke(): KoreDataType = create(DoubleType, GeoPackageDataType.DOUBLE)
}

/**
 * Variable length string encoded in either UTF-8 or UTF-16.
 * The optional `maxCharCount defines the maximum number of characters in the string.
 * If not specified, the length is unbounded.
 * The count is provided for informational purposes, and applications MAY choose to truncate longer strings
 * if encountered.
 */
object TextType : KoreClassImpl() {
    init {
        superTypes.add(GpkgDataType)
        name = "TEXT"
        attribute { name = "maxCharCount" }
    }

    operator fun invoke(init: KoreDataType.() -> Unit = {}): KoreDataType =
        create(TextType, GeoPackageDataType.TEXT).apply {
            init()
            maxCharCount?.let {
                name += "($it)"
            }
        }
}

var KoreDataType.maxCharCount: Long? by KoreStorage()

/**
 * Variable length binary data.
 * The optional `maxSize`defines the maximum number of bytes in the blob.
 * If not specified, the length is unbounded. The size is provided for informational purposes.
 */
object BlobType : KoreClassImpl() {
    init {
        superTypes.add(GpkgDataType)
        name = "BLOB"
        attribute { name = "maxSize" }
    }

    operator fun invoke(init: KoreDataType.() -> Unit = {}): KoreDataType =
        create(BlobType, GeoPackageDataType.BLOB).apply {
            init()
            maxSize?.let {
                name += "($it)"
            }
        }
}

var KoreDataType.maxSize: Long? by KoreStorage()

/**
 * ISO-8601 date string in the form YYYY-MM-DD encoded in either UTF-8 or UTF-16.
 * Stored as SQLite TEXT.
 */
object DateType : KoreClassImpl() {
    init {
        superTypes.add(TextType)
        name = "DATE"
    }

    operator fun invoke(): KoreDataType = create(DateType, GeoPackageDataType.DATE)
}

/**
 * ISO-8601 date/time string in the form YYYY-MM-DDTHH:MM:SS.SSSZ with T separator character and Z suffix
 * for coordinated universal time (UTC) encoded in either UTF-8 or UTF-16.
 * Stored as SQLite TEXT.
 */
object DateTimeType : KoreClassImpl() {
    init {
        superTypes.add(TextType)
        name = "DATETIME"
    }

    operator fun invoke(): KoreDataType = create(DateTimeType, GeoPackageDataType.DATETIME)
}

object GeometryType : KoreClassImpl() {
    init {
        superTypes.add(BlobType)
        name = "GEOMETRY"
    }

    operator fun invoke(): KoreDataType = create(GeometryType, GeometryType.GEOMETRY)
}

object PointType : KoreClassImpl() {
    init {
        superTypes.add(GeometryType)
        name = "POINT"
    }

    operator fun invoke(): KoreDataType = create(PointType, GeometryType.POINT)
}

object CurveType : KoreClassImpl() {
    init {
        superTypes.add(GeometryType)
        name = "CURVE"
    }

    operator fun invoke(): KoreDataType = create(CurveType, GeometryType.CURVE)
}

object SurfaceType : KoreClassImpl() {
    init {
        superTypes.add(GeometryType)
        name = "SURFACE"
    }

    operator fun invoke(): KoreDataType = create(SurfaceType, GeometryType.SURFACE)
}

object GeomCollectionType : KoreClassImpl() {
    init {
        superTypes.add(GeometryType)
        name = "GEOMETRYCOLLECTION"
    }

    operator fun invoke(): KoreDataType = create(GeomCollectionType, GeometryType.GEOMETRYCOLLECTION)
}

object LineStringType : KoreClassImpl() {
    init {
        superTypes.add(CurveType)
        name = "LINESTRING"
    }

    operator fun invoke(): KoreDataType = create(LineStringType, GeometryType.LINESTRING)
}

object CircularStringType : KoreClassImpl() {
    init {
        superTypes.add(CurveType)
        name = "CIRCULARSTRING"
    }

    operator fun invoke(): KoreDataType = create(CircularStringType, GeometryType.CIRCULARSTRING)
}

object CompoundCurveType : KoreClassImpl() {
    init {
        superTypes.add(CurveType)
        name = "COMPOUNDCURVE"
    }

    operator fun invoke(): KoreDataType = create(CompoundCurveType, GeometryType.COMPOUNDCURVE)
}

object CurvePolygonType : KoreClassImpl() {
    init {
        superTypes.add(SurfaceType)
        name = "CURVEPOLYGON"
    }

    operator fun invoke(): KoreDataType = create(CurvePolygonType, GeometryType.CURVEPOLYGON)
}

object PolygonType : KoreClassImpl() {
    init {
        superTypes.add(CurvePolygonType)
        name = "POLYGON"
    }

    operator fun invoke(): KoreDataType = create(PolygonType, GeometryType.POLYGON)
}

object MultiPointType : KoreClassImpl() {
    init {
        superTypes.add(GeomCollectionType)
        name = "MULTIPOINT"
    }

    operator fun invoke(): KoreDataType = create(MultiPointType, GeometryType.MULTIPOINT)
}

object MultiCurveType : KoreClassImpl() {
    init {
        superTypes.add(GeomCollectionType)
        name = "MULTICURVE"
    }

    operator fun invoke(): KoreDataType = create(MultiCurveType, GeometryType.MULTICURVE)
}

object MultiSurfaceType : KoreClassImpl() {
    init {
        superTypes.add(GeomCollectionType)
        name = "MULTISURFACE"
    }

    operator fun invoke(): KoreDataType = create(MultiSurfaceType, GeometryType.MULTISURFACE)
}

object MultiLineStringType : KoreClassImpl() {
    init {
        superTypes.add(MultiCurveType)
        name = "MULTILINESTRING"
    }

    operator fun invoke(): KoreDataType = create(MultiLineStringType, GeometryType.MULTILINESTRING)
}

object MultiPolygonType : KoreClassImpl() {
    init {
        superTypes.add(MultiSurfaceType)
        name = "MULTIPOLYGON"
    }

    operator fun invoke(): KoreDataType = create(MultiPolygonType, GeometryType.MULTIPOLYGON)
}
