/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.impl.ViolationLevel.CRITICAL
import es.iaaa.kore.resource.ResourceFactory
import es.iaaa.kore.transform.Property
import java.io.File

/**
 * Indicates whether a instance validated successfully against
 * a set of validations and if not, provides details indicating why not.
 *
 * @property [violations] List of constraint violations.
 */
open class Violations internal constructor(
    violations: List<Violation> = listOf()
) : Iterable<Violation> by violations {

    /**
     * The internal list of violations.
     */
    val violations: MutableList<Violation> = violations.toMutableList()

    /**
     * Returns `true` if a [CRITICAL] violation is found; otherwise `false`.
     */
    fun isValid(): Boolean = violations.none { it.level == CRITICAL }
}

/**
 * Describes a violation.
 *
 * @property [message] A description of the cause of the violation.
 * @property [level] The level of the violation.
 */
class Violation(
    private val message: String? = null,
    val level: ViolationLevel = CRITICAL
) : Violations()

/**
 * Sugar for a single violation.
 */
fun singleViolation(message: String? = null, level: ViolationLevel = CRITICAL): Violations =
    Violations(listOf(Violation(message, level)))

/**
 * Marker for validable classes.
 */
interface Validable {
    /**
     * Tests if there is not a critical error.
     */
    fun isValid(): Boolean = validate().isValid()

    /**
     * Returns a [Violations] object indicating if this input
     * is valid, and if not, provides details of the violations found.
     */
    fun validate(): Violations
}

/**
 * Violation levels.
 */
enum class ViolationLevel {
    /**
     * The system must halt; the error cannot be fixed at runtime.
     */
    CRITICAL,

    /**
     * This is an error that can be fixed at runtime.
     */
    ERROR,

    /**
     * This is a potential problem.
     */
    WARNING
}

/**
 * Checks if a file exists.
 */
fun Violations.validateFileExists(name: Property<String>) {
    if (name.isInitialized() && !File(name.get()).exists()) {
        violations.add(Violation("File $name does not exist", level = CRITICAL))
    }
}

/**
 * Checks if a factory exists.
 */
fun Violations.validateFactoryExists(factory: Property<String>) {
    if (factory.isInitialized() && !ResourceFactory.factories.containsKey(factory.get())) {
        violations.add(Violation("Factory $factory does not exist", level = CRITICAL))
    }
}

fun <T : Any> Violations.validateIsInitialized(property: Property<T>) {
    if (!property.isInitialized()) {
        violations.add(Violation("Property ${property.name} is not initialized", level = CRITICAL))
    }
}
