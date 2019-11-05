package es.iaaa.kore

/**
 * A representation of an operation.
 */
interface KoreOperation : KoreTypedElement {

    /**
     * When the operation represents a method.
     */
    var containingClass: KoreClass?

    /**
     * Represents the valid arguments for this operation.
     */
    val parameters: List<KoreParameter>

    /**
     * Returns true if this operation is an override of some other operation.
     */
    fun isOverrideOf(someOperation: KoreOperation): Boolean
}