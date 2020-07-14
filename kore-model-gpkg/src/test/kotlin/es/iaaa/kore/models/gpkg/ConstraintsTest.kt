/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.util.toPrettyString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ConstraintsTest {
    @Test
    fun `creation of range constraint`() {
        container {
            val constraint = rangeConstraint("test_range") {
                description = "some description"
                minRange = (-180).toBigDecimal()
                maxRange = 180.toBigDecimal()
                minIsInclusive = true
                maxIsInclusive = true
            }
            assertEquals(RangeConstraint, constraint.metaClass)
            assertEquals("test_range", constraint.name)
            assertEquals("some description", constraint.description)
            assertEquals((-180).toBigDecimal(), constraint.minRange)
            assertEquals(180.toBigDecimal(), constraint.maxRange)
            assertEquals(true, constraint.minIsInclusive)
            assertEquals(true, constraint.maxIsInclusive)
            assertEquals(
                """
           |Constraint Range "test_range" {
           |  description = "some description"
           |  maxIsInclusive = "true"
           |  maxRange = "180"
           |  minIsInclusive = "true"
           |  minRange = "-180"
           |}
           """.trimMargin(),
                constraint.toPrettyString()
            )
        }
    }

    @Test
    fun `creation of glob constraint`() {
        container {
            val constraint = globConstraint("test_glob") {
                description = "some description"
                globValue = "[0..9]*"
            }
            assertEquals(GlobConstraint, constraint.metaClass)
            assertEquals("test_glob", constraint.name)
            assertEquals("some description", constraint.description)
            assertEquals("[0..9]*", constraint.globValue)
            assertEquals(
                """
           |Constraint Glob "test_glob" {
           |  description = "some description"
           |  globValue = "[0..9]*"
           |}
           """.trimMargin(),
                constraint.toPrettyString()
            )
        }
    }

    @Test
    fun `creation of enum constraint`() {
        container {
            val constraint = enumConstraint("test_enum") {
                literal("a") {
                    description = "some description of a"
                }
                literal("b") {
                    description = "some description of b"
                }
            }
            assertEquals(EnumConstraint, constraint.metaClass)
            assertEquals("test_enum", constraint.name)
            assertEquals("some description of a", constraint.attributes[0].description)
            assertEquals("some description of b", constraint.attributes[1].description)
            assertEquals(
                """
           |Constraint Enum "test_enum" {
           |  a
           |  b
           |}
           """.trimMargin(),
                constraint.toPrettyString()
            )
        }
    }

    @Test
    fun `finding a constraint`() {
        val container = container {
            val constraint = rangeConstraint("test_range") {
                description = "some description"
                minRange = (-180).toBigDecimal()
                maxRange = 180.toBigDecimal()
                minIsInclusive = true
                maxIsInclusive = true
            }
            assertTrue(Constraint.isInstance(constraint))
            features("test_contents") {
                tableName = "test_contents_table"
                identifier = "test contents"
                description = "some description"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
                column {
                    name = "id"; columnName = "test_id"; title = ""; description = ""; lowerBound = 1; type =
                    IntegerType; geoPackageSpec().add(PrimaryKey)
                }
                column {
                    name = "geom"; columnName = "test_geom"; title = ""; description = ""; lowerBound = 1; type =
                    GeometryType
                }
                column {
                    name = "text"; columnName = "test_text"; title = ""; description = ""; lowerBound = 1; type =
                    TextType; defaultValueLiteral = ""
                }
                column { name = "real"; columnName = "test_real"; title = ""; description = ""; type = DoubleType }
                column {
                    name = "boolean"; columnName = "test_boolean"; title = ""; description = ""; type = BooleanType
                }
                column { name = "blob"; columnName = "test_blob"; title = ""; description = ""; type = BlobType }
                column {
                    name = "integer_column"; columnName = "test_integer_column"; title = ""; description = ""; type =
                    IntegerType; geoPackageSpec().add(constraint)
                }
                column {
                    name = "text_limited"; columnName = "test_text_limited"; title = ""; description = ""; type =
                    TextType { maxCharCount = 5 }
                }
                column {
                    name = "blob_limited"; columnName = "test_blob_limited"; title = ""; description = ""; mimeType =
                    ""; type = BlobType { maxSize = 7 }
                }
                column { name = "date"; columnName = "test_date"; type = DateType }
                column { name = "datetime"; columnName = "test_datetime"; type = DateTimeType }
            }
        }
        val candidate = container.allContents().filterIsInstance<KoreAttribute>()
            .filter { it.findGeoPackageSpec()?.any { Constraint.isInstance(it) } == true }.toList().getOrElse(0) { fail() }
        val constraint = candidate.geoPackageSpec().first {Constraint.isInstance(it) } as? KoreClass ?: fail()
        assertTrue(Constraint.isInstance(constraint))
    }

    @Test
    fun `testing equality`() {
        assertEquals(Constraint, Constraint)
        assertTrue(Constraint in listOf(Constraint))
        assertTrue(Constraint in EnumConstraint.superTypes)
    }

}
