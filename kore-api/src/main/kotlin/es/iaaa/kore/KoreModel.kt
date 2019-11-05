/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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


