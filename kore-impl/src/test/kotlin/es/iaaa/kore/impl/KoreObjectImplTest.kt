/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreModel
import es.iaaa.kore.KoreObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class KoreObjectImplTest {

    @Test
    fun `property metaClass`() {
        createObject().testPropertySingleValueUnsettable("metaClass", KoreModel.createClass())
    }

    @Test
    fun `property container`() {
        createObject().testPropertySingleValueUnchangeable("container", KoreModel.createClass())
    }

    @Test
    fun `property isLink`() {
        createObject().testPropertySingleValued("isLink", true)
    }

    @Test
    fun `private properties cannot be accessed`() {
        val exception = assertThrows<IllegalArgumentException> {
            createObject().isSet("store")
        }
        assertEquals("The feature 'store' is not allowed for this object", exception.message)
    }

    private fun createObject(): KoreObject = object : KoreObjectImpl() {
        override val container: KoreObject? = null
        override val contents: List<KoreObject> = emptyList()
    }
}