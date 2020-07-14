/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * The root of all modeled objects.
 * Provides support to all modeled objects.
 */
interface KoreObject {

    /**
     * Returns the identifier of this object.
     */
    var id: String?

    /**
     * Indicates if this object refer to another [KoreObject] with the same [id].
     */
    var isLink: Boolean

    /**
     * Returns the meta class.
     */
    var metaClass: KoreClass?

    /**
     * Returns the containing object or null.
     * An object is contained by another object if it appears in the [contents] of that object.
     */
    val container: KoreObject?

    /**
     * Returns a list view of the content objects.
     * Objects can, indirectly, be removed and will change to reflect container changes.
     */
    val contents: List<KoreObject>

    /**
     * A function that returns a representation of the object as [String].
     */
    var toString: ((KoreObject) -> String)?

    /**
     * Returns a sequence that iterates over all the direct contents and indirect contents of this object.
     */
    fun allContents(): Sequence<KoreObject>

    /**
     * Removes a single element of the specified [feature] of this object.
     */
    fun <T> remove(feature: String, element: T): Boolean

    /**
     * Adds a single element of the specified [feature] of this object.
     */
    fun <T> add(feature: String, element: T): Boolean

    /**
     * Returns whether the object is an instance of this object.
     */
    fun isInstance(obj: Any?): Boolean

    /**
     * Sets the value of the given feature of the object to the new value.
     */
    operator fun <T> set(feature: String, element: T)

    /**
     * Returns the value of the given feature of the object.
     */
    operator fun <T> get(feature: String): T?

    /**
     * Returns if the feature of the object is considered set.
     */
    fun isSet(feature: String): Boolean

    /**
     * Unsets the feature of the object. If the feature is single valued and nullable,
     * the modeled state becomes unset.
     */
    fun unset(feature: String)
}

/**
 * Checks if an annotation of a [KoreObject] references to a named element.
 */
fun KoreObject.references(name: String, source: String? = null): Boolean = when (this) {
    is KoreModelElement -> getAnnotation(source)
        ?.references
        ?.filterIsInstance<KoreNamedElement>()
        ?.any { it.name == name }
        ?: false
    else -> false
}

/**
 * Checks if an annotation of a [KoreObject] references to a named element.
 */
fun KoreObject.removeReference(name: String, source: String? = null) {
    if (this is KoreModelElement) {
        getAnnotation(source)?.references?.removeIf { item ->
            item is KoreNamedElement && item.name == name
        }
    }
}

/**
 * Checks if an annotation of a [KoreObject] references to none.
 */
fun KoreObject.hasNoReferences(source: String? = null): Boolean = when (this) {
    is KoreModelElement -> getAnnotation(source)
        ?.references
        ?.filterIsInstance<KoreNamedElement>()
        ?.isEmpty()
        ?: true
    else -> true
}

/**
 * Test if this object is a [KoreNamedElement] with [name] as its name.
 */
infix fun KoreObject.nameIs(name: String): Boolean = this is KoreNamedElement && this.name == name
