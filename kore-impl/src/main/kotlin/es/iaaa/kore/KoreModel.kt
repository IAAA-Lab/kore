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
fun KoreAnnotation.copy(modelElement: KoreModelElement? = this.modelElement): KoreAnnotation =
    koreAnnotation {
        source = this@copy.source
        details.putAll(this@copy.details)
        this.modelElement = modelElement
        references.addAll(this@copy.references)
        this@copy.annotations.map { ann -> ann.copy(this) }
    }

fun KoreAttribute.copy(target: KoreClass? = this.containingClass): KoreAttribute =
    koreAttribute(copyFromTo(this, target))

fun koreClass(block: KoreClass.() -> Unit): KoreClass = KoreModel.createClass().apply(block)
fun koreAttribute(block: KoreAttribute.() -> Unit): KoreAttribute = KoreModel.createAttribute().apply(block)
fun koreReference(block: KoreReference.() -> Unit): KoreReference = KoreModel.createReference().apply(block)
fun koreAnnotation(block: KoreAnnotation.() -> Unit): KoreAnnotation = KoreModel.createAnnotation().apply(block)

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