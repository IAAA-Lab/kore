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

class AttributeTableTest {
    @Test
    fun `creation of the attribute and its human readable representation`() {
        container {
            val attribute = attributes("test_contents") {
                identifier = "test contents"
                description = "some description"
                tableName = "test_contents"
            }
            assertEquals(AttributesTable, attribute.metaClass)
            assertEquals("test_contents", attribute.tableName)
            assertEquals("test contents", attribute.identifier)
            assertEquals("some description", attribute.description)
            assertEquals(
                """
           |Attributes "test_contents" {
           |  description = "some description"
           |  identifier = "test contents"
           |  tableName = "test_contents"
           |}
           """.trimMargin(),
                attribute.toPrettyString()
            )
        }
    }

    @Test
    fun `creation of a container with an attribute`() {
        val container = container {
            fileName = "test"
            attributes("test_contents") {
                identifier = "test contents"
                description = "some description"
                tableName = "test_contents"
            }
        }
        assertEquals(Container, container.metaClass)
        assertEquals("test", container.fileName)
        assertEquals(
            """
           |Container {
           |  Attributes "test_contents" {
           |    description = "some description"
           |    identifier = "test contents"
           |    tableName = "test_contents"
           |  }
           |}
           """.trimMargin(),
            container.toPrettyString()
        )
    }
}
