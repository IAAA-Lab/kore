package es.iaaa.kore

/**
 * The enumeration data type
 */
interface KoreEnum : KoreDataType {

    /**
     * Represents the enumerators of the enumeration.
     */
    val literals: List<KoreEnumLiteral>

    /**
     * Return the enum literal by [name].
     */
    fun findEnumLiteral(name: String): KoreEnumLiteral?

    /**
     * Return the enum literal by [value].
     */
    fun findEnumLiteral(value: Int): KoreEnumLiteral?

    /**
     * Return the enum literal by [literal].
     */
    fun findEnumLiteralByLiteral(literal: String): KoreEnumLiteral?
}