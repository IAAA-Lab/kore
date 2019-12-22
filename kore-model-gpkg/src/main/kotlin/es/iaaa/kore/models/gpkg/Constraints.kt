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
import es.iaaa.kore.impl.KoreClassImpl

object Constraint : KoreClassImpl() {
    init {
        name = "Constraint"
        attribute { name = "description" }
        attribute { name = "identifier" }
    }
}

/**
 * Constraint that specifies a range between a min and a max value.
 */
object RangeConstraint : KoreClassImpl() {
    init {
        superTypes.add(Constraint)
        name = "Constraint Range"
        attribute { name = "maxIsInclusive" }
        attribute { name = "minIsInclusive" }
        attribute { name = "minRange" }
        attribute { name = "maxRange" }
    }

    operator fun invoke(init: KoreClass.() -> Unit): KoreClass = koreClass {
        metaClass = RangeConstraint
        init()
    }
}

/**
 * A short hand factory function.
 */
fun rangeConstraint(name: String, init: KoreClass.() -> Unit) = RangeConstraint(init).also {
    it.name = name
}

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.rangeConstraint(name: String, init: KoreClass.() -> Unit) = RangeConstraint(init).also {
    it.name = name
    it.container = this
}

/**
 * Constraint that specifies a glob.
 */
object GlobConstraint : KoreClassImpl() {
    init {
        superTypes.add(Constraint)
        name = "Constraint Glob"
        attribute { name = "globValue" }
    }

    operator fun invoke(init: KoreClass.() -> Unit): KoreClass = koreClass {
        metaClass = GlobConstraint
        init()
    }
}

/**
 * A short hand factory function.
 */
fun globConstraint(name: String, init: KoreClass.() -> Unit) = GlobConstraint(init).also {
    it.name = name
}

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.globConstraint(name: String, init: KoreClass.() -> Unit) = GlobConstraint(init).also {
    it.name = name
    it.container = this
}

/**
 * Constraint that specifies a glob.
 */
object EnumConstraint : KoreClassImpl() {
    init {
        superTypes.add(Constraint)
        name = "Constraint Enum"
    }

    operator fun invoke(init: KoreClass.() -> Unit): KoreClass = koreClass {
        metaClass = EnumConstraint
        init()
    }
}

/**
 * A short hand factory function.
 */
fun enumConstraint(name: String, init: KoreClass.() -> Unit) = EnumConstraint(init).also {
    it.name = name
}

/**
 * A short hand factory function with container addition.
 */
fun KorePackage.enumConstraint(name: String, init: KoreClass.() -> Unit) = EnumConstraint(init).also {
    it.name = name
    it.container = this
}

/**
 * Attribute
 */
object EnumConstraintValue : KoreClassImpl() {
    init {
        superTypes.add(Constraint)
        name = "Constraint Enum Literal"
    }

    operator fun invoke(init: KoreAttribute.() -> Unit): KoreAttribute = KoreModel.createAttribute().apply {
        metaClass = EnumConstraintValue
        lowerBound = 1
        upperBound = 1
        init()
    }
}

/**
 * A short hand factory function with container addition.
 */
fun KoreClass.literal(name: String, init: KoreAttribute.() -> Unit) = EnumConstraintValue(init).also {
    it.name = name
    it.containingClass = this
}
