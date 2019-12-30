package es.iaaa.kore

import es.iaaa.kore.impl.KoreInstanceInternal

object KoreModel : KoreModelFactory by KoreInstanceInternal()

fun KoreReference.toAttribute(remove: Boolean = true): KoreAttribute =
    koreAttribute(copyFromTo(this, containingClass, remove = remove))

fun KoreAttribute.toReference(remove: Boolean = true): KoreReference =
    koreReference(copyFromTo(this, containingClass, remove = remove))

/**
 * Copy an annotation.
 */
fun KoreAnnotation.copy(modelElement: KoreModelElement): KoreAnnotation =
    modelElement.koreAnnotation {
        source = this@copy.source
        details.putAll(this@copy.details)
        references.addAll(this@copy.references)
        this@copy.annotations.forEach { ann -> ann.copy(this) }
    }

fun KoreAttribute.copy(target: KoreClass? = this.containingClass): KoreAttribute =
    koreAttribute(copyFromTo(this, target))

fun koreClass(block: KoreClass.() -> Unit): KoreClass = KoreModel.createClass().apply(block)

fun koreClass(meta: KoreClass, block: KoreClass.() -> Unit): KoreClass = KoreModel.createClass().apply {
    metaClass = meta
    block()
    verify(metaClass == meta) { "The metaclass property has muted within the block"}
}

fun koreDataType(block: KoreDataType.() -> Unit): KoreDataType = KoreModel.createDataType().apply(block)
fun koreClassifier(block: KoreClassifier.() -> Unit): KoreClassifier = KoreModel.createClassifier().apply(block)

fun koreAttribute(block: KoreAttribute.() -> Unit): KoreAttribute = KoreModel.createAttribute().apply(block)
fun koreAttribute(meta: KoreClass, block: KoreAttribute.() -> Unit): KoreAttribute = KoreModel.createAttribute().apply {
    metaClass = meta
    block()
    verify(metaClass == meta) { "The metaclass property has muted within the block"}
}

fun KoreClass.attribute(init: KoreAttribute.() -> Unit) = koreAttribute {
    containingClass = this@attribute
    init()
    verify( containingClass == this@attribute) { "The containing class has muted within the block"}
}

fun korePackage(meta: KoreClass, block: KorePackage.() -> Unit) = KoreModel.createPackage().apply {
    metaClass = meta
    block()
    verify(metaClass == meta) { "The metaclass property has muted within the block"}
}

fun koreReference(block: KoreReference.() -> Unit): KoreReference = KoreModel.createReference().apply(block)

fun koreReference(meta: KoreClass, block: KoreReference.() -> Unit) = KoreModel.createReference().apply {
    metaClass = meta
    block()
    verify(metaClass == meta) { "The metaclass property has muted within the block"}
}

fun KoreModelElement.koreAnnotation(block: KoreAnnotation.() -> Unit): KoreAnnotation = KoreModel.createAnnotation().apply {
    modelElement = this@koreAnnotation
    block()
    verify(modelElement == this@koreAnnotation) { "The modelElement property has muted within the block"}
}

    fun copyFromTo(
        other: KoreStructuralFeature,
        target: KoreClass?,
        remove: Boolean = false
    ): KoreStructuralFeature.() -> Unit = {
        metaClass = other.metaClass
        isChangeable = other.isChangeable
        isUnsettable = other.isUnsettable
        defaultValueLiteral = other.defaultValueLiteral
        ordered = other.ordered
        lowerBound = other.lowerBound
        upperBound = other.upperBound
        type = other.type
        name = other.name
        other.annotations.map { ann -> ann.copy(this) }
        if (remove) {
            other.containingClass = null
        }
        containingClass = target
    }

fun KoreModelElement.stereotype(stereotype: String) {
    if (!references(stereotype)) {
        findOrCreateAnnotation().references.add(koreClass { name = stereotype })
    }
}

