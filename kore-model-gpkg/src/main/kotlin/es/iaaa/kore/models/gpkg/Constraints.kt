/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
