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
 * A representation of a class.
 */
interface KoreClass : KoreClassifier {
    /**
     * True if this class does not provide support for creating an instance.
     */
    var isAbstract: Boolean

    /**
     * True if this class contains only abstract declarations. Implies [isAbstract] true.
     */
    var isInterface: Boolean

    /**
     * The super classes local to this class in the broadest sense.
     */
    var superTypes: MutableList<KoreClass>

    /**
     * The closure of all super classes, inherited and local.
     */
    fun allSuperTypes(): List<KoreClass>

    /**
     * Returns the modeled features local to this class.
     */
    val structuralFeatures: List<KoreStructuralFeature>

    /**
     * Returns the modeled attributes local to this class.
     */
    val attributes: List<KoreAttribute>

    /**
     * The closure of all attributes, inherited and local.
     */
    fun allAttributes(): List<KoreAttribute>

    /**
     * Returns the modeled references local to this class.
     */
    val references: List<KoreReference>

    /**
     * The closure of all references, inherited and local.
     */
    fun allReferences(): List<KoreReference>

    /**
     * The closure of all references, inherited and local, that are containments.
     */
    fun allContainments(): List<KoreReference>

    /**
     * The closure of all attributes and references, inherited and local.
     */
    fun allStructuralFeatures(): List<KoreStructuralFeature>

    /**
     * The operations local to this class.
     */
    val operations: List<KoreOperation>

    /**
     * The closure of all operations, inherited or local.
     */
    fun allOperations(): List<KoreOperation>

    /**
     * Returns if this class is the same as, or supertype of, some other class
     */
    fun isSuperTypeOf(someClass: KoreClass?): Boolean

    /**
     * Find a structural feature inherited or local with this [name].
     */
    fun findStructuralFeature(name: String): KoreStructuralFeature?

    /**
     * Find an operation with this [name].
     */
    fun findOperation(name: String): KoreOperation?
}