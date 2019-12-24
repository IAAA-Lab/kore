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
