package es.iaaa.kore

import es.iaaa.kore.impl.KoreInstanceInternal

object KoreModel : KoreModelFactory by KoreInstanceInternal()

fun KoreReference.toAttribute(): KoreAttribute {
    val cls = containingClass
    containingClass = null
    return KoreModel.createAttribute().apply {
        metaClass = this@toAttribute.metaClass
        isChangeable = this@toAttribute.isChangeable
        isUnsettable = this@toAttribute.isUnsettable
        defaultValueLiteral = this@toAttribute.defaultValueLiteral
        containingClass = cls
        ordered = this@toAttribute.ordered
        lowerBound = this@toAttribute.lowerBound
        upperBound = this@toAttribute.upperBound
        type = this@toAttribute.type
        name = this@toAttribute.name
        this@toAttribute.annotations.map { ann -> ann.copy(this) }
    }
}

fun KoreAttribute.toReference(): KoreReference {
    val cls = containingClass
    containingClass = null
    return KoreModel.createReference().apply {
        metaClass = this@toReference.metaClass
        isChangeable = this@toReference.isChangeable
        isUnsettable = this@toReference.isUnsettable
        defaultValueLiteral = this@toReference.defaultValueLiteral
        containingClass = cls
        ordered = this@toReference.ordered
        lowerBound = this@toReference.lowerBound
        upperBound = this@toReference.upperBound
        type = this@toReference.type
        name = this@toReference.name
        this@toReference.annotations.map { ann -> ann.copy(this) }
    }
}

/**
 * Copy an annotation.
 */
fun KoreAnnotation.copy(modelElement: KoreModelElement? = this.modelElement): KoreAnnotation =
    KoreModel.createAnnotation().also {
        it.source = source
        it.details.putAll(details)
        it.modelElement = modelElement
        it.references.addAll(references)
        annotations.map { ann -> ann.copy(it) }
    }

fun KoreAttribute.copy(target: KoreClass): KoreAttribute =
    KoreModel.createAttribute().apply {
        isChangeable = this@copy.isChangeable
        isUnsettable = this@copy.isUnsettable
        defaultValueLiteral = this@copy.defaultValueLiteral
        containingClass = target
        ordered = this@copy.ordered
        lowerBound = this@copy.lowerBound
        upperBound = this@copy.upperBound
        type = this@copy.type
        name = this@copy.name
        this@copy.annotations.map { ann -> ann.copy(this) }
    }
