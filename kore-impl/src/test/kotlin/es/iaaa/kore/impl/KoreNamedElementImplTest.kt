package es.iaaa.kore.impl

import es.iaaa.kore.KoreModel
import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.KoreObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class KoreNamedElementImplTest {

    @Test
    fun `access to inherited property metaClass`() {
        createNamedElement().testPropertySingleValueUnsettable("metaClass", KoreModel.createClass())
    }

    @Test
    fun `access to local property name`() {
        createNamedElement().testPropertySingleValueUnsettable("name", "test")
    }

    @Test
    fun `inherited private properties cannot be accessed as expected`() {
        val exception = assertThrows<IllegalArgumentException> {
            createNamedElement().isSet("store")
        }
        assertEquals("The feature 'store' is not allowed for this object", exception.message)
    }

    private fun createNamedElement(): KoreNamedElement = object : KoreNamedElementImpl() {
        override val container: KoreObject? get() = null
        override val contents: List<KoreObject> = emptyList()
    }
}

