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

class FeatureTableTest {
    @Test
    fun `creation of the feature and its human readable representation`() {
        container {
            val feature = features("test_contents") {
                identifier = "test contents"
                description = "some description"
                tableName = "test_contents"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
            }
            assertEquals(FeaturesTable, feature.metaClass)
            assertEquals("test_contents", feature.tableName)
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
           |  tableName = "test_contents"
           |}
           """.trimMargin(),
                feature.toPrettyString()
            )
        }
    }

    @Test
    fun `creation of a container with a feature`() {
        val container = container {
            fileName = "test"
            features("test_contents") {
                identifier = "test contents"
                description = "some description"
                tableName = "test_contents"
                minX = -180.0
                maxX = 180.0
                minY = -90.0
                maxY = 90.0
                srsId = 0
            }
        }
        assertEquals(Container, container.metaClass)
        assertEquals("test", container.fileName)
        assertEquals(
            """
           |Container {
           |  Feature "test_contents" {
           |    description = "some description"
           |    identifier = "test contents"
           |    maxX = "180.0"
           |    maxY = "90.0"
           |    minX = "-180.0"
           |    minY = "-90.0"
           |    srsId = "0"
           |    tableName = "test_contents"
           |  }
           |}
           """.trimMargin(),
            container.toPrettyString()
        )
    }
}
