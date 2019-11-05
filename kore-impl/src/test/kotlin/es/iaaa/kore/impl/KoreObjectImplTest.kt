package es.iaaa.kore.impl

import es.iaaa.kore.KoreModel
import es.iaaa.kore.KoreObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

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

    private fun createObject(): KoreObject = object: KoreObjectImpl() {
        override val container: KoreObject? = null
        override val contents: List<KoreObject> = emptyList()
    }
}