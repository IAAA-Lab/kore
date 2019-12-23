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

import es.iaaa.kore.util.toPrettyString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ColumnTest {
    @Test
    fun `creation of the feature and its human readable representation`() {
        container {
            val constraint = rangeConstraint("test_range") {
                description = "some description"
                minRange = (-180).toBigDecimal()
                maxRange = 180.toBigDecimal()
                minIsInclusive = true
                maxIsInclusive = true
            }
            val feature = features("test_contents") {
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
            assertEquals(FeaturesTable, feature.metaClass)
            assertEquals("test_contents_table", feature.tableName)
            assertEquals("test contents", feature.identifier)
            assertEquals("some description", feature.description)
            assertEquals(-180.0, feature.minX)
            assertEquals(180.0, feature.maxX)
            assertEquals(-90.0, feature.minY)
            assertEquals(90.0, feature.maxY)
            assertEquals(0, feature.srsId)
            assertEquals(
                """
           |Feature "test_contents" {
           |  description = "some description"
           |  identifier = "test contents"
           |  maxX = "180.0"
           |  maxY = "90.0"
           |  minX = "-180.0"
           |  minY = "-90.0"
           |  srsId = "0"
           |  tableName = "test_contents_table"
           |  test_blob BLOB
           |  test_blob_limited BLOB(7)
           |  test_boolean BOOLEAN
           |  test_date DATE
           |  test_datetime DATETIME
           |  test_geom GEOMETRY NOT NULL
           |  test_id INTEGER NOT NULL PRIMARY KEY
           |  test_integer_column INTEGER CHECK(test_range)
           |  test_real DOUBLE
           |  test_text TEXT NOT NULL DEFAULT ''
           |  test_text_limited TEXT(5)
           |}
           """.trimMargin(),
                feature.toPrettyString()
            )
        }
    }
}
