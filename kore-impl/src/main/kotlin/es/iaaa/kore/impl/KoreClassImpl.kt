/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.*

open class KoreClassImpl : KoreClassifierImpl(), KoreClass {
    override var isAbstract: Boolean = false
    override var isInterface: Boolean = false
    override var superTypes: MutableList<KoreClass> = mutableListOf()
    override fun allSuperTypes(): List<KoreClass> {
        tailrec fun expand(original: List<KoreClass>, expanded: List<KoreClass>): List<KoreClass> =
            when (original) {
                expanded -> expanded
                else -> expand(expanded, (expanded + expanded.flatMap { it.superTypes }).toSet().toList())
            }
        return expand(emptyList(), superTypes)
    }

    override val structuralFeatures: List<KoreStructuralFeature> get() = internalStructuralFeatures.toList()
    override val attributes: List<KoreAttribute> get() = internalStructuralFeatures.filterIsInstance<KoreAttribute>().toList()
    override fun allAttributes(): List<KoreAttribute> =
        allStructuralFeatures().filterIsInstance<KoreAttribute>().toList()

    override val references: List<KoreReference> get() = internalStructuralFeatures.filterIsInstance<KoreReference>().toList()
    override fun allReferences(): List<KoreReference> =
        allStructuralFeatures().filterIsInstance<KoreReference>().toList()

    override fun allContainments(): List<KoreReference> = allReferences().filter { it.isContainement }
    override fun allStructuralFeatures(): List<KoreStructuralFeature> = structuralFeatures + allSuperTypes()
        .flatMap { it.structuralFeatures }
        .filter { inherited -> structuralFeatures.find { it.name == inherited.name } == null }

    override val operations: List<KoreOperation> get() = internalOperations.toList()
    override fun allOperations(): List<KoreOperation> {
        val candidates = operations + allSuperTypes().flatMap { it.operations }
        return candidates.filter { op -> candidates.any { it.isOverrideOf(op) } }
    }

    override fun isSuperTypeOf(someClass: KoreClass?): Boolean =
        someClass != null && (someClass == this || someClass.allSuperTypes().contains(this))

    override fun findStructuralFeature(name: String): KoreStructuralFeature? =
        allStructuralFeatures().find { it.name == name }

    override fun findOperation(name: String): KoreOperation? = allOperations().find { it.name == name }
    override val contents: List<KoreObject> get() = super.contents + structuralFeatures + operations
    override fun allContents(): Sequence<KoreObject> = sequence {
        yieldAll(super.contents)
        yieldAll(allStructuralFeatures())
        yieldAll(allOperations())
    }

    override fun isInstance(obj: Any?): Boolean =
        when (obj) {
            is KoreClass ->
                when {
                    // Obj is a subclass of this (structural or referential link)
                    obj.allSuperTypes().contains(this) -> true
                    obj.allSuperTypes().map { it.id }.contains(id) -> true
                    // Obj metaclass is this or any of its supertypes is this (structural or referential link)
                    obj.metaClass == this -> true
                    obj.metaClass?.id == id -> true
                    obj.metaClass?.allSuperTypes()?.contains(this) ?: false -> true
                    obj.metaClass?.allSuperTypes()?.map { it.id }?.contains(id) ?: false -> true
                    else -> super.isInstance(obj)
                }
            is KoreDataType ->
                when {
                    // Obj metaclass is this or any of its supertypes is this (structural or referential link)
                    obj.metaClass == this -> true
                    obj.metaClass?.id == id -> true
                    obj.metaClass?.allSuperTypes()?.contains(this) ?: false -> true
                    obj.metaClass?.allSuperTypes()?.map { it.id }?.contains(id) ?: false -> true
                    else -> super.isInstance(obj)
                }
            else -> super.isInstance(obj)
        }

    // FIXME Move to KoreObjectImpl and use the storage
    override fun <T> add(feature: String, element: T): Boolean = when (feature) {
        KoreClass::structuralFeatures.name -> if (element is KoreStructuralFeature) internalStructuralFeatures.add(
            element
        ) else false
        else -> super.add(feature, element)
    }

    // FIXME Move to KoreObjectImpl and use the storage
    override fun <T> remove(feature: String, element: T): Boolean = when (feature) {
        KoreClass::structuralFeatures.name -> if (element is KoreStructuralFeature) internalStructuralFeatures.remove(
            element
        ) else false
        else -> super.remove(feature, element)
    }

    internal val internalOperations: MutableList<KoreOperationImpl> = mutableListOf()
    private val internalStructuralFeatures: MutableList<KoreStructuralFeature> = mutableListOf()

    override fun toString(): String = "class ${container?.name}#$name"
}
