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

class ContainerTest {
    @Test
    fun `creation of the container and its human readable representation`() {
        val container = container {
            fileName = "test"
        }
        assertEquals(Container, container.metaClass)
        assertEquals("test", container.fileName)
        assertEquals(
            """
           |Container {}
           """.trimMargin(),
            container.toPrettyString()
        )
    }
}
