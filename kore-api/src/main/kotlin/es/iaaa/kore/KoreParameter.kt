package es.iaaa.kore

/**
 * The representation of a parameter of a [KoreOperation].
 */
interface KoreParameter : KoreTypedElement {

    /**
     * It represents the containing operation.
     */
    var operation: KoreOperation?
}