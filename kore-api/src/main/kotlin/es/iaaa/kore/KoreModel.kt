/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore

/**
 * Kore model factory.
 */
interface KoreModelFactory : KoreFactory {
    /**
     * Returns a new [KoreAnnotation].
     */
    fun createAnnotation(): KoreAnnotation

    /**
     * Returns a new [KoreClassifier].
     */
    fun createClassifier(): KoreClassifier

    /**
     * Returns a new [KorePackage].
     */
    fun createPackage(): KorePackage

    /**
     * Returns a new [KoreClass].
     */
    fun createClass(): KoreClass

    /**
     * Returns a new [KoreDataType].
     */
    fun createDataType(): KoreDataType

    /**
     * Returns a new [KoreEnum].
     */
    fun createEnum(): KoreEnum

    /**
     * Returns a new [KoreEnumLiteral].
     */
    fun createEnumLiteral(): KoreEnumLiteral

    /**
     * Returns a new [KoreAttribute].
     */
    fun createAttribute(): KoreAttribute

    /**
     * Returns a new [KoreReference].
     */
    fun createReference(): KoreReference

    /**
     * Returns a new [KoreOperation].
     */
    fun createOperation(): KoreOperation

    /**
     * Returns a new [KoreParameter].
     */
    fun createParameter(): KoreParameter
}


