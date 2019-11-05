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

interface KorePackage : KoreNamedElement {
    /**
     * The unique identification of a particular package.
     */
    var nsUri: String?

    /**
     * The preferred acronym of the [nsUri].
     */
    var nsPrefix: String?

    /**
     * The meta objects defined in this package.
     */
    val classifiers: List<KoreClassifier>

    /**
     * The nexted packages contained by this package.
     */
    val subpackages: List<KorePackage>

    /**
     * The containing package of this package
     */
    var superPackage: KorePackage?

    /**
     * Returns the classifier with the given [name].
     */
    fun findClassifier(name: String): KoreClassifier?

    /**
     * Return al subpackages, local and contained in subpackages.
     */
    fun allSubpackages(): Sequence<KorePackage>

    /**
     * Return all classifiers, local and contained in all subpackages.
     */
    fun allClassifiers(): Sequence<KoreClassifier>
}