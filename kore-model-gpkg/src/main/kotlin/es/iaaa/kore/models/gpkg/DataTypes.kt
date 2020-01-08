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

import es.iaaa.kore.*
import es.iaaa.kore.impl.KoreStorage
import mil.nga.geopackage.db.GeoPackageDataType
import mil.nga.sf.GeometryType

/**
 * Common metaclass of GeoPackage Data types.
 */
object GpkgDataType : KoreClass by koreClass({
    attribute { name = "ngaGeoPackageDataType" }
    attribute { name = "ngaGeometryType" }
})

/**
 * Reference to the identifier in the NGA implementation.
 */
var KoreDataType.ngaGeoPackageDataType: GeoPackageDataType? by KoreStorage()

fun create(parent: KoreClass, type: GeoPackageDataType): KoreDataType = koreDataType {
    metaClass = parent
    name = parent.name
    ngaGeoPackageDataType = type
}

/**
 * Reference to the identifier in the NGA implementation.
 */
var KoreDataType.ngaGeometryType: GeometryType? by KoreStorage()

fun create(parent: KoreClass, type: GeometryType): KoreDataType = koreDataType {
    metaClass = parent
    name = parent.name
    ngaGeometryType = type
}

/**
 * A boolean value representing true or false.
 * Stored as SQLite INTEGER with value 0 for false or 1 for true.
 */
object BooleanType : KoreClass by koreClass({
    superTypes.add(IntegerType)
    name = "BOOLEAN"
}) {
    operator fun invoke(): KoreDataType = create(this, GeoPackageDataType.BOOLEAN)
}

/**
 * 8-bit signed two’s complement integer.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object TinyIntType : KoreClass by koreClass({
    superTypes.add(IntegerType)
    name = "TINYINT"
}) {
    operator fun invoke(): KoreDataType = create(TinyIntType, GeoPackageDataType.TINYINT)
}

/**
 * 16-bit signed two’s complement integer.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object SmallIntType : KoreClass by koreClass({
    superTypes.add(IntegerType)
    name = "SMALLINT"
}) {
    operator fun invoke(): KoreDataType = create(SmallIntType, GeoPackageDataType.SMALLINT)
}

/**
 * 32-bit signed two’s complement integer.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object MediumIntType : KoreClass by koreClass({
    superTypes.add(IntegerType)
    name = "MEDIUMINT"
}) {
    operator fun invoke(): KoreDataType = create(MediumIntType, GeoPackageDataType.MEDIUMINT)
}

/**
 * 64-bit signed two’s complement integer.
 * Stored as SQLite INTEGER with values in the range [-128, 127].
 */
object IntegerType : KoreClass by koreClass({
    superTypes.add(GpkgDataType)
    name = "INTEGER"
}) {
    operator fun invoke(): KoreDataType = create(IntegerType, GeoPackageDataType.INTEGER)
}

/**
 * 32-bit IEEE floating point number.
 */
object FloatType : KoreClass by koreClass({
    superTypes.add(DoubleType)
    name = "FLOAT"
}) {
    operator fun invoke(): KoreDataType = create(FloatType, GeoPackageDataType.FLOAT)
}

/**
 * 64-bit IEEE floating point number.
 * Stored as SQLite REAL.
 */
object DoubleType : KoreClass by koreClass({
    superTypes.add(GpkgDataType)
    name = "DOUBLE"
}) {
    operator fun invoke(): KoreDataType = create(DoubleType, GeoPackageDataType.DOUBLE)
}

/**
 * 64-bit IEEE floating point number.
 * Stored as SQLite REAL.
 */
object RealType : KoreClass by koreClass({
    superTypes.add(GpkgDataType)
    name = "REAL"
}) {
    operator fun invoke(): KoreDataType = create(RealType, GeoPackageDataType.REAL)
}

/**
 * Variable length string encoded in either UTF-8 or UTF-16.
 * The optional `maxCharCount defines the maximum number of characters in the string.
 * If not specified, the length is unbounded.
 * The count is provided for informational purposes, and applications MAY choose to truncate longer strings
 * if encountered.
 */
object TextType : KoreClass by koreClass({
    superTypes.add(GpkgDataType)
    name = "TEXT"
    attribute { name = "maxCharCount" }
}) {
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
object BlobType : KoreClass by koreClass({
    superTypes.add(GpkgDataType)
    name = "BLOB"
    attribute { name = "maxSize" }
}) {
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
object DateType : KoreClass by koreClass({
    superTypes.add(TextType)
    name = "DATE"
}) {
    operator fun invoke(): KoreDataType = create(DateType, GeoPackageDataType.DATE)
}

/**
 * ISO-8601 date/time string in the form YYYY-MM-DDTHH:MM:SS.SSSZ with T separator character and Z suffix
 * for coordinated universal time (UTC) encoded in either UTF-8 or UTF-16.
 * Stored as SQLite TEXT.
 */
object DateTimeType : KoreClass by koreClass({
    superTypes.add(TextType)
    name = "DATETIME"
}) {
    operator fun invoke(): KoreDataType = create(DateTimeType, GeoPackageDataType.DATETIME)
}

object GeometryType : KoreClass by koreClass({
    superTypes.add(BlobType)
    name = "GEOMETRY"
}) {
    operator fun invoke(): KoreDataType = create(GeometryType, GeometryType.GEOMETRY)
}

object PointType : KoreClass by koreClass({
    superTypes.add(GeometryType)
    name = "POINT"
}) {
    operator fun invoke(): KoreDataType = create(PointType, GeometryType.POINT)
}

object CurveType : KoreClass by koreClass({
    superTypes.add(GeometryType)
    name = "CURVE"
}) {
    operator fun invoke(): KoreDataType = create(CurveType, GeometryType.CURVE)
}

object SurfaceType : KoreClass by koreClass({
    superTypes.add(GeometryType)
    name = "SURFACE"
}) {
    operator fun invoke(): KoreDataType = create(SurfaceType, GeometryType.SURFACE)
}

object GeomCollectionType : KoreClass by koreClass({
    superTypes.add(GeometryType)
    name = "GEOMETRYCOLLECTION"
}) {
    operator fun invoke(): KoreDataType = create(GeomCollectionType, GeometryType.GEOMETRYCOLLECTION)
}

object LineStringType : KoreClass by koreClass({
    superTypes.add(CurveType)
    name = "LINESTRING"
}) {
    operator fun invoke(): KoreDataType = create(LineStringType, GeometryType.LINESTRING)
}

object CircularStringType : KoreClass by koreClass({
    superTypes.add(CurveType)
    name = "CIRCULARSTRING"
}) {
    operator fun invoke(): KoreDataType = create(CircularStringType, GeometryType.CIRCULARSTRING)
}

object CompoundCurveType : KoreClass by koreClass({
    superTypes.add(CurveType)
    name = "COMPOUNDCURVE"
}) {
    operator fun invoke(): KoreDataType = create(CompoundCurveType, GeometryType.COMPOUNDCURVE)
}

object CurvePolygonType : KoreClass by koreClass({
    superTypes.add(SurfaceType)
    name = "CURVEPOLYGON"
}) {
    operator fun invoke(): KoreDataType = create(CurvePolygonType, GeometryType.CURVEPOLYGON)
}

object PolygonType : KoreClass by koreClass({
    superTypes.add(CurvePolygonType)
    name = "POLYGON"
}) {
    operator fun invoke(): KoreDataType = create(PolygonType, GeometryType.POLYGON)
}

object MultiPointType : KoreClass by koreClass({
    superTypes.add(GeomCollectionType)
    name = "MULTIPOINT"
}) {
    operator fun invoke(): KoreDataType = create(MultiPointType, GeometryType.MULTIPOINT)
}

object MultiCurveType : KoreClass by koreClass({
    superTypes.add(GeomCollectionType)
    name = "MULTICURVE"
}) {
    operator fun invoke(): KoreDataType = create(MultiCurveType, GeometryType.MULTICURVE)
}

object MultiSurfaceType : KoreClass by koreClass({
    superTypes.add(GeomCollectionType)
    name = "MULTISURFACE"
}) {
    operator fun invoke(): KoreDataType = create(MultiSurfaceType, GeometryType.MULTISURFACE)
}

object MultiLineStringType : KoreClass by koreClass({
    superTypes.add(MultiCurveType)
    name = "MULTILINESTRING"
}) {
    operator fun invoke(): KoreDataType = create(MultiLineStringType, GeometryType.MULTILINESTRING)
}

object MultiPolygonType : KoreClass by koreClass({
    superTypes.add(MultiSurfaceType)
    name = "MULTIPOLYGON"
}) {
    operator fun invoke(): KoreDataType = create(MultiPolygonType, GeometryType.MULTIPOLYGON)
}
