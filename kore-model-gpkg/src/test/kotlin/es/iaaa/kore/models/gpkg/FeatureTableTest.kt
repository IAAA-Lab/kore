/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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
