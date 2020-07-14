/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform

/**
 * A safe way to define properties for builder.
 */
class Property<T : Any>(val name: String, private val default: T? = null) {

    private lateinit var value: T

    fun get(): T = if (::value.isInitialized) {
        value
    } else default ?: throw NullPointerException("Property $name is not initialized")

    fun isInitialized(): Boolean = ::value.isInitialized || default != null

    fun set(newValue: T) {
        if (!::value.isInitialized) {
            value = newValue
        }
    }
}
