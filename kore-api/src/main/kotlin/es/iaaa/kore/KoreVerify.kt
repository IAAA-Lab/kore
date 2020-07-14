/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
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
