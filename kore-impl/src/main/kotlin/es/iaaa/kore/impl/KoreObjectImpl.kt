/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KoreObject
import es.iaaa.kore.KoreStructuralFeature
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

abstract class KoreObjectImpl : KoreObject {
    private val internalId: UUID = UUID.randomUUID()
    private val store = mutableMapOf<String, Any?>()
    override var metaClass: KoreClass? = null
    override var id: String? = null
        get() = if (field == null) "KORE_$internalId" else field
    override var isLink: Boolean = false
    override var toString: ((KoreObject) -> String)? = null

    /**
     * TODO: Consider the case of changeable collections
     */
    override fun <T> set(feature: String, element: T) {
        val property: KProperty1<Any, T>? = discoverProperty(feature)
        if (property != null) {
            when {
                !property.isChangeable -> throw IllegalArgumentException("The feature '$feature' is unchangeable")
                !property.isUnsettable && element == null -> throw IllegalArgumentException("The feature '$feature' is not unsettable")
                else -> (property as KMutableProperty1<*, *>).setter.call(this, element)
            }
        } else {
            val metaProperty = resolveProperty(feature)
            validateValue(metaProperty, element)
            store[feature] = element
        }
    }

    override fun <T> get(feature: String): T {
        val property: KProperty1<Any, T>? = discoverProperty(feature)
        if (property != null) {
            return property.get(this)
        } else {
            val metaProperty = resolveProperty(feature)
            val value = store.getOrDefault(feature, metaProperty.defaultValue)
            validateValue(metaProperty, value)
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
    }

    /**
     * TODO: The logic of set requires to known default (initial values) for single values
     *  and test the size changeable collections for many values.
     */
    override fun isSet(feature: String): Boolean {
        val property: KProperty1<Any, Any?>? = discoverProperty(feature)
        return if (property != null) {
            property.get(this) != null
        } else {
            val metaProperty = resolveProperty(feature)
            store.getOrDefault(feature, metaProperty.defaultValue) != null
        }
    }

    /**
     * TODO: The logic of unset requires to known default (initial values) for single values
     *  and clear changeable collections for many values.
     */
    override fun unset(feature: String) {
        val property: KProperty1<Any, Any?>? = discoverProperty(feature)
        if (property != null) {
            when {
                !property.isChangeable -> throw IllegalArgumentException("The feature '$feature' is unchangeable")
                !property.isUnsettable -> throw IllegalArgumentException("The feature '$feature' is not unsettable")
                else -> (property as KMutableProperty1<*, *>).setter.call(this, null)
            }
        } else {
            val metaProperty = resolveProperty(feature)
            if (metaProperty.isUnsettable) {
                throw IllegalArgumentException("The feature '$feature' is unsettable")
            }
            store.remove(feature)
        }
    }

    override fun allContents(): Sequence<KoreObject> = sequence {
        yieldAll(contents)
        contents.forEach { yieldAll(it.allContents()) }
    }

    override fun <T> remove(feature: String, element: T): Boolean {
        throw IllegalArgumentException("The feature '$feature' is not a valid feature")
    }

    override fun <T> add(feature: String, element: T): Boolean {
        throw IllegalArgumentException("The feature '$feature' is not a valid feature")
    }

    override fun isInstance(obj: Any?): Boolean =
        if (obj is KoreObject) {
            when {
                obj == this || obj.metaClass == this -> true
                obj.metaClass is KoreClassifier -> isInstance(obj.metaClass)
                this == KoreRoot -> true
                else -> false
            }
        } else false

    private fun resolveProperty(feature: String): KoreStructuralFeature {
        val localResolution = metaClass?.findStructuralFeature(feature)
        return localResolution
            ?: throw IllegalArgumentException("The feature '$feature' is not allowed for this object")
    }

    private fun <T> discoverMutableProperty(feature: String): KMutableProperty1<Any, T>? {
        @Suppress("UNCHECKED_CAST")
        return this::class.memberProperties.find { it.name == feature } as? KMutableProperty1<Any, T>
    }

    private fun <T> discoverProperty(feature: String): KProperty1<Any, T>? {
        @Suppress("UNCHECKED_CAST")
        return this::class.memberProperties.find { it.name == feature } as? KProperty1<Any, T>
    }

    private fun validateValue(property: KoreStructuralFeature, value: Any?) {
        val type = property.type
        type?.let { if (!type.isInstance(value)) throw IllegalArgumentException("'${property.name}' : '${type.name}' can't accept '$value'") }
    }
}

class ReadWriteStorage<T> : ReadWriteProperty<KoreObject, T> {
    override fun getValue(thisRef: KoreObject, property: KProperty<*>): T = (thisRef as KoreObjectImpl)[property.name]

    override fun setValue(thisRef: KoreObject, property: KProperty<*>, value: T) {
        (thisRef as KoreObjectImpl)[property.name] = value
    }
}

@Suppress("FunctionName")
inline fun <reified T> KoreStorage() = ReadWriteStorage<T>()