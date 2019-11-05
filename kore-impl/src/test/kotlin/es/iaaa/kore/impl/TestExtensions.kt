package es.iaaa.kore.impl

import es.iaaa.kore.KoreObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.full.memberProperties

/**
 * Reflective access to the object that represents the property.
 */
fun KoreObject.getKProperty(name: String) = this::class.memberProperties.find { it.name == name }
    ?: throw IllegalArgumentException("FIXME: '$name' is not a member property")

/**
 * Reflective access to the value of a property.
 */
fun KoreObject.getProperty(name: String): Any? = getKProperty(name).call(this)

/**
 * Test different operations related to the access to a property.
 */
fun <T> KoreObject.testPropertySingleValueUnsettable(name: String, value: T) {
    assertFalse(isSet(name))
    set(name, value)
    assertTrue(isSet(name))
    assertEquals(value, getProperty(name))
    assertEquals(value, this[name])
    unset(name)
    assertFalse(isSet(name))
    assertNull(getProperty(name))
    assertNull(get(name))
}

/**
 * Test different operations related to the access to a property.
 */
fun <T> KoreObject.testPropertySingleValueUnchangeable(name: String, value: T) {
    assertFalse(isSet(name))
    var exception = assertThrows<IllegalArgumentException> {
        this@testPropertySingleValueUnchangeable[name] = value
    }
    assertEquals("The feature '$name' is unchangeable", exception.message)
    assertFalse(isSet(name))
    assertNull(getProperty(name))
    assertNull(this[name])
    exception = assertThrows {
        this@testPropertySingleValueUnchangeable.unset(name)
    }
    assertEquals("The feature '$name' is unchangeable", exception.message)
    assertFalse(isSet(name))
    assertNull(getProperty(name))
    assertNull(get(name))
}

/**
 * Test different operations related to the access to a property.
 */
fun <T> KoreObject.testPropertySingleValued(name: String, value: T) {
    assertTrue(isSet(name))
    set(name, value)
    assertTrue(isSet(name))
    assertEquals(value, getProperty(name))
    assertEquals(value, this[name])
    val exception = assertThrows<IllegalArgumentException> {
        unset(name)
    }
    assertEquals("The feature '$name' is not unsettable", exception.message)
    assertTrue(isSet(name))
    assertNotNull(getProperty(name))
    assertNotNull(get(name))
}
