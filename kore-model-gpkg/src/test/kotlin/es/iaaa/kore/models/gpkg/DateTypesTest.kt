/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import mil.nga.geopackage.db.GeoPackageDataType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DateTypesTest {

    @Test
    fun `boolean data type`() {
        assertTrue(GpkgDataType.isInstance(BooleanType))
        assertEquals("BOOLEAN", BooleanType.name)
        assertEquals(GeoPackageDataType.BOOLEAN, BooleanType().ngaGeoPackageDataType)
    }

    @Test
    fun `tinyint data type`() {
        assertTrue(GpkgDataType.isInstance(TinyIntType))
        assertEquals("TINYINT", TinyIntType.name)
        assertEquals(GeoPackageDataType.TINYINT, TinyIntType().ngaGeoPackageDataType)
    }

    @Test
    fun `smallint data type`() {
        assertTrue(GpkgDataType.isInstance(SmallIntType))
        assertEquals("SMALLINT", SmallIntType.name)
        assertEquals(GeoPackageDataType.SMALLINT, SmallIntType().ngaGeoPackageDataType)
    }

    @Test
    fun `mediumint data type`() {
        assertTrue(GpkgDataType.isInstance(MediumIntType))
        assertEquals("MEDIUMINT", MediumIntType.name)
        assertEquals(GeoPackageDataType.MEDIUMINT, MediumIntType().ngaGeoPackageDataType)
    }

    @Test
    fun `integer data type`() {
        assertTrue(GpkgDataType.isInstance(IntegerType))
        assertEquals("INTEGER", IntegerType.name)
        assertEquals(GeoPackageDataType.INTEGER, IntegerType().ngaGeoPackageDataType)
    }

    @Test
    fun `float data type`() {
        assertTrue(GpkgDataType.isInstance(FloatType))
        assertEquals("FLOAT", FloatType.name)
        assertEquals(GeoPackageDataType.FLOAT, FloatType().ngaGeoPackageDataType)
    }

    @Test
    fun `double data type`() {
        assertTrue(GpkgDataType.isInstance(DoubleType))
        assertEquals("DOUBLE", DoubleType.name)
        assertEquals(GeoPackageDataType.DOUBLE, DoubleType().ngaGeoPackageDataType)
    }

    @Test
    fun `text data type`() {
        assertTrue(GpkgDataType.isInstance(TextType()))
        assertEquals("TEXT", TextType().name)
        assertEquals(GeoPackageDataType.TEXT, TextType().ngaGeoPackageDataType)

        val ownTextType = TextType {
            maxCharCount = 5
        }
        assertTrue(GpkgDataType.isInstance(ownTextType))
        assertTrue(TextType.isInstance(ownTextType))
        assertEquals("TEXT(5)", ownTextType.name)
        assertEquals(GeoPackageDataType.TEXT, ownTextType.ngaGeoPackageDataType)
        assertEquals(5, ownTextType.maxCharCount)
    }

    @Test
    fun `blob data type`() {
        assertTrue(GpkgDataType.isInstance(BlobType()))
        assertEquals("BLOB", BlobType().name)
        assertEquals(GeoPackageDataType.BLOB, BlobType().ngaGeoPackageDataType)

        val ownBlobType = BlobType {
            maxSize = 5
        }
        assertTrue(GpkgDataType.isInstance(ownBlobType))
        assertTrue(BlobType.isInstance(ownBlobType))
        assertEquals("BLOB(5)", ownBlobType.name)
        assertEquals(GeoPackageDataType.BLOB, ownBlobType.ngaGeoPackageDataType)
        assertEquals(5, ownBlobType.maxSize)
    }

    @Test
    fun `date data type`() {
        assertTrue(GpkgDataType.isInstance(DateType))
        assertEquals("DATE", DateType.name)
        assertEquals(GeoPackageDataType.DATE, DateType().ngaGeoPackageDataType)
    }

    @Test
    fun `date time data type`() {
        assertTrue(GpkgDataType.isInstance(DateTimeType))
        assertEquals("DATETIME", DateTimeType.name)
        assertEquals(GeoPackageDataType.DATETIME, DateTimeType().ngaGeoPackageDataType)
    }

    @Test
    fun `geometry type`() {
        assertTrue(GpkgDataType.isInstance(GeometryType))
        assertEquals("GEOMETRY", GeometryType.name)
        assertEquals(mil.nga.sf.GeometryType.GEOMETRY, GeometryType().ngaGeometryType)
    }

    @Test
    fun `point type`() {
        assertTrue(GpkgDataType.isInstance(PointType))
        assertTrue(GeometryType.isInstance(PointType))
        assertEquals("POINT", PointType.name)
        assertEquals(mil.nga.sf.GeometryType.POINT, PointType().ngaGeometryType)
    }

    @Test
    fun `linestring type`() {
        assertTrue(GpkgDataType.isInstance(LineStringType))
        assertTrue(CurveType.isInstance(LineStringType))
        assertEquals("LINESTRING", LineStringType.name)
        assertEquals(mil.nga.sf.GeometryType.LINESTRING, LineStringType().ngaGeometryType)
    }

    @Test
    fun `polygon type`() {
        assertTrue(GpkgDataType.isInstance(PolygonType))
        assertTrue(CurvePolygonType.isInstance(PolygonType))
        assertEquals("POLYGON", PolygonType.name)
        assertEquals(mil.nga.sf.GeometryType.POLYGON, PolygonType().ngaGeometryType)
    }

    @Test
    fun `multipoint type`() {
        assertTrue(GpkgDataType.isInstance(MultiPointType))
        assertTrue(GeomCollectionType.isInstance(MultiPointType))
        assertEquals("MULTIPOINT", MultiPointType.name)
        assertEquals(mil.nga.sf.GeometryType.MULTIPOINT, MultiPointType().ngaGeometryType)
    }

    @Test
    fun `multilinestring type`() {
        assertTrue(GpkgDataType.isInstance(MultiLineStringType))
        assertTrue(MultiCurveType.isInstance(MultiLineStringType))
        assertEquals("MULTILINESTRING", MultiLineStringType.name)
        assertEquals(mil.nga.sf.GeometryType.MULTILINESTRING, MultiLineStringType().ngaGeometryType)
    }

    @Test
    fun `multipolygon type`() {
        assertTrue(GpkgDataType.isInstance(MultiPolygonType))
        assertTrue(MultiSurfaceType.isInstance(MultiPolygonType))
        assertEquals("MULTIPOLYGON", MultiPolygonType.name)
        assertEquals(mil.nga.sf.GeometryType.MULTIPOLYGON, MultiPolygonType().ngaGeometryType)
    }

    @Test
    fun `geometrycollection type`() {
        assertTrue(GpkgDataType.isInstance(GeomCollectionType))
        assertTrue(GeometryType.isInstance(GeomCollectionType))
        assertEquals("GEOMETRYCOLLECTION", GeomCollectionType.name)
        assertEquals(mil.nga.sf.GeometryType.GEOMETRYCOLLECTION, GeomCollectionType().ngaGeometryType)
    }

    @Test
    fun `circularstring type`() {
        assertTrue(GpkgDataType.isInstance(CircularStringType))
        assertTrue(CurveType.isInstance(CircularStringType))
        assertEquals("CIRCULARSTRING", CircularStringType.name)
        assertEquals(mil.nga.sf.GeometryType.CIRCULARSTRING, CircularStringType().ngaGeometryType)
    }

    @Test
    fun `compoundcurve type`() {
        assertTrue(GpkgDataType.isInstance(CompoundCurveType))
        assertTrue(CurveType.isInstance(CompoundCurveType))
        assertEquals("COMPOUNDCURVE", CompoundCurveType.name)
        assertEquals(mil.nga.sf.GeometryType.COMPOUNDCURVE, CompoundCurveType().ngaGeometryType)
    }

    @Test
    fun `curvepolygon type`() {
        assertTrue(GpkgDataType.isInstance(CurvePolygonType))
        assertTrue(SurfaceType.isInstance(CurvePolygonType))
        assertEquals("CURVEPOLYGON", CurvePolygonType.name)
        assertEquals(mil.nga.sf.GeometryType.CURVEPOLYGON, CurvePolygonType().ngaGeometryType)
    }

    @Test
    fun `multicurve type`() {
        assertTrue(GpkgDataType.isInstance(MultiCurveType))
        assertTrue(GeomCollectionType.isInstance(MultiCurveType))
        assertEquals("MULTICURVE", MultiCurveType.name)
        assertEquals(mil.nga.sf.GeometryType.MULTICURVE, MultiCurveType().ngaGeometryType)
    }

    @Test
    fun `multisurface type`() {
        assertTrue(GpkgDataType.isInstance(MultiSurfaceType))
        assertTrue(GeomCollectionType.isInstance(MultiSurfaceType))
        assertEquals("MULTISURFACE", MultiSurfaceType.name)
        assertEquals(mil.nga.sf.GeometryType.MULTISURFACE, MultiSurfaceType().ngaGeometryType)
    }

    @Test
    fun `curve type`() {
        assertTrue(GpkgDataType.isInstance(CurveType))
        assertTrue(GeometryType.isInstance(CurveType))
        assertEquals("CURVE", CurveType.name)
        assertEquals(mil.nga.sf.GeometryType.CURVE, CurveType().ngaGeometryType)
    }

    @Test
    fun `surface type`() {
        assertTrue(GpkgDataType.isInstance(SurfaceType))
        assertTrue(GeometryType.isInstance(SurfaceType))
        assertEquals("SURFACE", SurfaceType.name)
        assertEquals(mil.nga.sf.GeometryType.SURFACE, SurfaceType().ngaGeometryType)
    }
}
