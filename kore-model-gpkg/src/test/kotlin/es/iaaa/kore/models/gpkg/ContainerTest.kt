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
