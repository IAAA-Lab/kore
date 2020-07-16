/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.io.gpkg

import es.iaaa.kore.models.gpkg.*
import mil.nga.geopackage.extension.CrsWktExtension
import mil.nga.geopackage.extension.GeometryExtensions
import mil.nga.geopackage.extension.MetadataExtension
import mil.nga.geopackage.extension.SchemaExtension
import mil.nga.geopackage.extension.related.RelatedTablesExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class ContainerManagerTest {

    @TempDir
    @JvmField
    var folder: Path = Paths.get(".")

    @Test
    fun `create and open a container`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
        }
        File("${containerFile.path}.gpkg").exists()
        assertTrue(ContainerManager.create(sut), "Database failed to create")
        assertTrue(File("${containerFile.path}.gpkg").exists(), "Database does not exist")

        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        geoPackage.close()
    }

    @Test
    fun `verify metadata extension is installed`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
        }
        ContainerManager.create(sut)
        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertTrue(MetadataExtension(geoPackage).has())
        geoPackage.close()
    }

    @Test
    fun `verify non-linear types extension is installed`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            features("the_feature") {
                identifier = "test contents"
                tableName = "the_feature"
                description = "some description"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
                idColumn()
                column { columnName = "the_geom"; lowerBound = 1; type = CurvePolygonType() }
            }
        }
        ContainerManager.create(sut)
        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertFalse(
            geoPackage.extensionsDao.queryByExtension(GeometryExtensions.getExtensionName(mil.nga.sf.GeometryType.CURVEPOLYGON))
                .isEmpty()
        )
        geoPackage.close()
    }

    @Test
    fun `verify schema extension is installed`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
        }
        ContainerManager.create(sut)
        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertTrue(SchemaExtension(geoPackage).has())
        geoPackage.close()
    }

    @Test
    fun `verify related tables extension is installed`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            val attribute1 = attributes("test_contents_1") {
                tableName = "test_contents_1"
                idColumn()
            }
            val attribute2 = attributes("test_contents_2") {
                tableName = "test_contents_2"
                idColumn()
            }
            relation("test_relations") {
                tableName = "test_relations"
                foreignColumn { name = "base_id"; type = attribute1; geoPackageSpec().add(BaseTable) }
                foreignColumn { name = "related_id"; type = attribute2; geoPackageSpec().add(RelatedTable) }
            }
        }
        ContainerManager.create(sut)
        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertTrue(RelatedTablesExtension(geoPackage).has())
        geoPackage.close()
    }

    @Test
    fun `verify CRS WKT extension is installed`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
        }
        ContainerManager.create(sut)
        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertTrue(CrsWktExtension(geoPackage).has())
        geoPackage.close()
    }

    @Test
    fun `create a feature`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            features("test_contents") {
                identifier = "test contents"
                description = "some description"
                tableName = "test_contents"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
                idColumn()
                column { columnName = "test_geom"; lowerBound = 1; type = GeometryType() }
                column { columnName = "test_text"; lowerBound = 1; type = TextType(); defaultValueLiteral = "" }
                column { columnName = "test_real"; type = DoubleType() }
                column { columnName = "test_boolean"; type = BooleanType() }
                column { columnName = "test_blob"; type = BlobType() }
                column { columnName = "test_integer_column"; type = IntegerType() }
                column { columnName = "test_text_limited"; type = TextType { maxCharCount = 5 } }
                column { columnName = "test_blob_limited"; type = BlobType { maxSize = 7 } }
                column { columnName = "test_date"; type = DateType() }
                column { columnName = "test_datetime"; type = DateTimeType() }
            }
        }
        assertTrue(ContainerManager.create(sut), "Database failed to create")

        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertEquals(1, geoPackage.featureTables.size, "Feature was not created")
        assertEquals("test_contents", geoPackage.featureTables[0])
        geoPackage.close()
    }

    @Test
    fun `create a feature with non linear geometry`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            features("test_contents") {
                identifier = "test contents"
                description = "some description"
                tableName = "test_contents"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
                idColumn()
                column { columnName = "test_geom"; lowerBound = 1; type = SurfaceType() }
            }
        }
        assertTrue(ContainerManager.create(sut), "Database failed to create")

        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertEquals(1, geoPackage.featureTables.size, "Feature was not created")
        assertEquals("test_contents", geoPackage.featureTables[0])
        val ext = geoPackage.extensionsDao.queryByExtension("gpkg_geom_SURFACE")[0]
        assertNotNull(ext)
        assertEquals("test_contents", ext.tableName)
        assertEquals("test_geom", ext.columnName)
        assertEquals("http://www.geopackage.org/spec/#extension_geometry_types", ext.definition)
        geoPackage.close()
    }

    @Test
    fun `create an attribute`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            attributes("test_contents") {
                identifier = "test contents"
                description = "some description"
                tableName = "test_contents"
                idColumn()
                column { columnName = "test_text"; lowerBound = 1; type = TextType(); defaultValueLiteral = "" }
                column { columnName = "test_real"; type = DoubleType() }
                column { columnName = "test_boolean"; type = BooleanType() }
                column { columnName = "test_blob"; type = BlobType() }
                column { columnName = "test_integer_column"; type = IntegerType() }
                column { columnName = "test_text_limited"; type = TextType { maxCharCount = 5 } }
                column { columnName = "test_blob_limited"; type = BlobType { maxSize = 7 } }
                column { columnName = "test_date"; type = DateType() }
                column { columnName = "test_datetime"; type = DateTimeType() }
            }
        }
        assertTrue(ContainerManager.create(sut), "Database failed to create")

        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertEquals(1, geoPackage.attributesTables.size, "Attribute was not created")
        assertEquals("test_contents", geoPackage.attributesTables[0])
        geoPackage.close()
    }

    @Test
    fun `create a set of constraints`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            rangeConstraint("test_range") {
                description = "some description"
                minRange = (-180).toBigDecimal()
                maxRange = 180.toBigDecimal()
                minIsInclusive = true
                maxIsInclusive = true
            }
            globConstraint("test_glob") {
                description = "some description"
                globValue = "[0..9]*"
            }
            enumConstraint("test_enum") {
                literal("a") {
                    description = "some description of a"
                }
                literal("b") {
                    description = "some description of b"
                }
            }
        }
        assertTrue(ContainerManager.create(sut), "Database failed to create")

        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertEquals(4, geoPackage.dataColumnConstraintsDao.queryForAll().size)
        geoPackage.close()
    }

    @Test
    fun `create a full definition of a column`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            val range = rangeConstraint("test_range") {
                description = "some description"
                minRange = (-180).toBigDecimal()
                maxRange = 180.toBigDecimal()
                minIsInclusive = true
                maxIsInclusive = true
            }
            attributes("test_attributes") {
                identifier = "test attributes"
                tableName = "test_attributes"
                description = "some description"
                idColumn()
                column {
                    name = "attributes.int"; title = "Integer"; description = "the int"; columnName =
                    "test_integer_column"; type = IntegerType(); geoPackageSpec().add(range)
                }
                column {
                    name = "attributes.blob"; title = "Blob"; description = "the blob"; columnName =
                    "text_blob"; mimeType = "blob"; type = BlobType()
                }
            }
            features("test_features") {
                identifier = "test features"
                description = "some description"
                tableName = "test_features"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
                idColumn()
                column {
                    name = "features.geom"; title = "Geom"; description = "the geom"; columnName =
                    "test_geom"; lowerBound = 1; type = GeometryType()
                }
                column {
                    name = "features.int"; title = "Int"; description = "the int"; columnName =
                    "test_integer_column"; type = IntegerType(); geoPackageSpec().add(range)
                }
            }
        }
        assertTrue(ContainerManager.create(sut), "Database failed to create")

        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertEquals(1, geoPackage.dataColumnConstraintsDao.queryForAll().size)
        assertEquals(6, geoPackage.dataColumnsDao.queryForAll().size)
        geoPackage.close()
    }

    @Test
    fun `creation of the relation and its human readable representation`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            val attribute1 = attributes("test_contents_1") {
                tableName = "test_contents_1"
                description = ""
                idColumn()
            }
            val attribute2 = attributes("test_contents_2") {
                tableName = "test_contents_2"
                description = ""
                idColumn()
            }
            relation("test_relations") {
                tableName = "test_relations"
                foreignColumn { name = "base_id"; type = attribute1; geoPackageSpec().add(BaseTable) }
                foreignColumn { name = "related_id"; type = attribute2; geoPackageSpec().add(RelatedTable) }
            }
        }
        assertTrue(ContainerManager.create(sut), "Database failed to create")

        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertEquals(1, geoPackage.extendedRelationsDao.queryForAll().size)
        geoPackage.close()
    }
}
