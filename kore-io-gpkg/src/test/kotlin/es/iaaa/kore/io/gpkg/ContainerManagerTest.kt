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
package es.iaaa.kore.io.gpkg

import es.iaaa.kore.models.gpkg.BaseTable
import es.iaaa.kore.models.gpkg.BlobType
import es.iaaa.kore.models.gpkg.BooleanType
import es.iaaa.kore.models.gpkg.DateTimeType
import es.iaaa.kore.models.gpkg.DateType
import es.iaaa.kore.models.gpkg.DoubleType
import es.iaaa.kore.models.gpkg.GeometryType
import es.iaaa.kore.models.gpkg.IntegerType
import es.iaaa.kore.models.gpkg.PrimaryKey
import es.iaaa.kore.models.gpkg.RelatedTable
import es.iaaa.kore.models.gpkg.SurfaceType
import es.iaaa.kore.models.gpkg.TextType
import es.iaaa.kore.models.gpkg.attributes
import es.iaaa.kore.models.gpkg.column
import es.iaaa.kore.models.gpkg.columnName
import es.iaaa.kore.models.gpkg.container
import es.iaaa.kore.models.gpkg.description
import es.iaaa.kore.models.gpkg.enumConstraint
import es.iaaa.kore.models.gpkg.feature
import es.iaaa.kore.models.gpkg.fileName
import es.iaaa.kore.models.gpkg.geoPackageSpec
import es.iaaa.kore.models.gpkg.globConstraint
import es.iaaa.kore.models.gpkg.globValue
import es.iaaa.kore.models.gpkg.identifier
import es.iaaa.kore.models.gpkg.literal
import es.iaaa.kore.models.gpkg.maxCharCount
import es.iaaa.kore.models.gpkg.maxIsInclusive
import es.iaaa.kore.models.gpkg.maxRange
import es.iaaa.kore.models.gpkg.maxSize
import es.iaaa.kore.models.gpkg.maxX
import es.iaaa.kore.models.gpkg.maxY
import es.iaaa.kore.models.gpkg.mimeType
import es.iaaa.kore.models.gpkg.minIsInclusive
import es.iaaa.kore.models.gpkg.minRange
import es.iaaa.kore.models.gpkg.minX
import es.iaaa.kore.models.gpkg.minY
import es.iaaa.kore.models.gpkg.rangeConstraint
import es.iaaa.kore.models.gpkg.reference
import es.iaaa.kore.models.gpkg.relation
import es.iaaa.kore.models.gpkg.srsId
import es.iaaa.kore.models.gpkg.title
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
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
    fun `create a feature`() {
        val containerFile = folder.resolve("test").toFile()
        val sut = container {
            fileName = containerFile.path
            feature("test_contents") {
                identifier = "test contents"
                description = "some description"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
                column {
                    columnName = "test_id"; lowerBound = 1; type = IntegerType(); geoPackageSpec().add(PrimaryKey)
                }
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
            feature("test_contents") {
                identifier = "test contents"
                description = "some description"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
                column {
                    columnName = "test_id"; lowerBound = 1; type = IntegerType(); geoPackageSpec().add(PrimaryKey)
                }
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
                column {
                    columnName = "test_id"; lowerBound = 1; type = IntegerType(); geoPackageSpec().add(PrimaryKey)
                }
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
                description = "some description"
                column {
                    name = "attributes.id"; title = "ID"; description = "the id"; columnName = "test_id"; lowerBound =
                    1; type = IntegerType(); geoPackageSpec().add(PrimaryKey)
                }
                column {
                    name = "attributes.int"; title = "Integer"; description = "the int"; columnName =
                    "test_integer_column"; type = IntegerType(); geoPackageSpec().add(range)
                }
                column {
                    name = "attributes.blob"; title = "Blob"; description = "the blob"; columnName =
                    "text_blob"; mimeType = "blob"; type = BlobType()
                }
            }
            feature("test_features") {
                identifier = "test features"
                description = "some description"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
                column {
                    name = "features.id"; title = "ID"; description = "the id"; columnName = "test_id"; lowerBound =
                    1; type = IntegerType(); geoPackageSpec().add(PrimaryKey)
                }
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
                column { columnName = "id"; type = IntegerType(); geoPackageSpec().add(PrimaryKey) }
            }
            val attribute2 = attributes("test_contents_2") {
                column { columnName = "id"; type = IntegerType(); geoPackageSpec().add(PrimaryKey) }
            }
            relation("test_relations") {
                reference { name = "base_id"; type = attribute1; geoPackageSpec().add(BaseTable) }
                reference { name = "related_id"; type = attribute2; geoPackageSpec().add(RelatedTable) }
            }
        }
        assertTrue(ContainerManager.create(sut), "Database failed to create")

        val geoPackage = ContainerManager.open(sut) ?: fail("Failed to open database")
        assertEquals(1, geoPackage.extendedRelationsDao.queryForAll().size)
        geoPackage.close()
    }
}
