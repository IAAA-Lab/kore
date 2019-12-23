package es.iaaa.kore

/**
 * Exception thrown upon the failure of a verification check.
 */
class KoreVerifyException(message: String) : Exception(message)

inline fun verify(value: Boolean, lazyMessage: () -> Any) {
    if (!value) {
        val message = lazyMessage()
        throw KoreVerifyException(message.toString())
    }
}
