/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

object Constraint : KoreClass by koreClass({
    name = "Constraint"
    attribute { name = "description" }
    attribute { name = "identifier" }
})

/**
 * Constraint that specifies a range between a min and a max value.
 */
object RangeConstraint : KoreClass by koreClass({
    superTypes.add(Constraint)
    name = "Constraint Range"
    attribute { name = "maxIsInclusive" }
    attribute { name = "minIsInclusive" }
    attribute { name = "minRange" }
    attribute { name = "maxRange" }
})



/**
 * A short hand factory function with container addition.
 */
fun KorePackage.rangeConstraint(nameConstraint: String, init: KoreClass.() -> Unit = {}) = koreClass(RangeConstraint) {
    name = nameConstraint
    container = this@rangeConstraint
    init()
    verify(name == nameConstraint) { "The name property has muted within the block" }
    verify(container == this@rangeConstraint) { "The container property has muted within the block" }
}

/**
 * Constraint that specifies a glob.
 */
object GlobConstraint : KoreClass by koreClass({
    superTypes.add(Constraint)
    name = "Constraint Glob"
    attribute { name = "globValue" }
}
)

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.globConstraint(nameConstraint: String, init: KoreClass.() -> Unit = {}) = koreClass(GlobConstraint) {
    name = nameConstraint
    container = this@globConstraint
    init()
    verify(name == nameConstraint) { "The name property has muted within the block" }
    verify(container == this@globConstraint) { "The container property has muted within the block" }
}

/**
 * Constraint that specifies a glob.
 */
object EnumConstraint : KoreClass by koreClass({
    superTypes.add(Constraint)
    name = "Constraint Enum"
})

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.enumConstraint(nameConstraint: String, init: KoreClass.() -> Unit = {}) = koreClass(EnumConstraint) {
    name = nameConstraint
    container = this@enumConstraint
    init()
    verify(name == nameConstraint) { "The name property has muted within the block" }
    verify(container == this@enumConstraint) { "The container property has muted within the block" }
}

/**
 * Attribute
 */
object EnumConstraintValue : KoreClass by koreClass({
    name = "Constraint Enum Literal"
    attribute { name = "description" }
})


/**
 * A short hand factory function with container addition.
 */
fun KoreClass.literal(literalName: String, init: KoreAttribute.() -> Unit = {}) = koreAttribute(EnumConstraintValue) {
    name = literalName
    containingClass = this@literal
    lowerBound = 1
    upperBound = 1
    init()
    verify(name == literalName) { "The name property has muted within the block" }
    verify(container == this@literal) { "The container property has muted within the block" }
    verify(lowerBound == 1) { "The lower bound property has muted within the block" }
    verify(upperBound == 1) { "The upper bound property has muted within the block" }
}
