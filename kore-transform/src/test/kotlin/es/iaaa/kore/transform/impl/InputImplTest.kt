/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

/**
 * Unit tests of [InputImpl]
 */
internal class InputImplTest {

    @Test
    fun `default implementation is not valid`() {
        with(InputImpl()) {
            assertFalse(isValid())
            assertEquals(2, validate().violations.size)
        }
    }
}