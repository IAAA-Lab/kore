/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KoreObject
import es.iaaa.kore.KorePackage

/**
 * A representation of the model object **Package**.
 */
internal open class KorePackageImpl : KoreNamedElementImpl(), KorePackage {
    override val contents: List<KoreObject> get() = super.contents + classifiers + subpackages
    override var nsUri: String? = null
    override var nsPrefix: String? = null
    override val container: KoreObject? get() = superPackage
    override val classifiers: MutableList<KoreClassifier> = mutableListOf()
    override val subpackages: MutableList<KorePackage> = mutableListOf()
    override var superPackage: KorePackage? = null
        set(value) {
            if (value != superPackage) {
                superPackage?.remove(KorePackage::subpackages.name, this)
                value?.add(KorePackage::subpackages.name, this)
                field = value
            }
        }

    override fun findClassifier(name: String): KoreClassifier? = classifiers.find { it.name == name }

    override fun allClassifiers(): Sequence<KoreClassifier> = sequence {
        yieldAll(classifiers)
        allSubpackages().forEach {
            yieldAll(it.classifiers)
        }
    }

    override fun allSubpackages(): Sequence<KorePackage> = sequence {
        yieldAll(subpackages)
        subpackages.forEach {
            yieldAll(it.allSubpackages())
        }
    }

    // FIXME Move to KoreObjectImpl and use the storage
    override fun <T> add(feature: String, element: T): Boolean = when (feature) {
        KorePackage::classifiers.name -> if (element is KoreClassifier) classifiers.add(element) else false
        KorePackage::subpackages.name -> if (element is KorePackage) subpackages.add(element) else false
        else -> super.add(feature, element)
    }

    // FIXME Move to KoreObjectImpl and use the storage
    override fun <T> remove(feature: String, element: T): Boolean = when (feature) {
        KorePackage::classifiers.name -> if (element is KoreClassifier) classifiers.remove(element) else false
        KorePackage::subpackages.name -> if (element is KorePackage) subpackages.remove(element) else false
        else -> super.remove(feature, element)
    }
}
