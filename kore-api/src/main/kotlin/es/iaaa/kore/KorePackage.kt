/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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