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

import es.iaaa.kore.util.toPrettyString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConstraintsTest {
    @Test
    fun `creation of range constraint`() {
        container {
            val constraint = rangeConstraint("test_range") {
                description = "some description"
                minRange = (-180).toBigDecimal()
                maxRange = 180.toBigDecimal()
                minIsInclusive = true
                maxIsInclusive = true
            }
            assertEquals(RangeConstraint, constraint.metaClass)
            assertEquals("test_range", constraint.name)
            assertEquals("some description", constraint.description)
            assertEquals((-180).toBigDecimal(), constraint.minRange)
            assertEquals(180.toBigDecimal(), constraint.maxRange)
            assertEquals(true, constraint.minIsInclusive)
            assertEquals(true, constraint.maxIsInclusive)
            assertEquals(
                """
           |Constraint Range "test_range" {
           |  description = "some description"
           |  maxIsInclusive = "true"
           |  maxRange = "180"
           |  minIsInclusive = "true"
           |  minRange = "-180"
           |}
           """.trimMargin(),
                constraint.toPrettyString()
            )
        }
    }

    @Test
    fun `creation of glob constraint`() {
        container {
            val constraint = globConstraint("test_glob") {
                description = "some description"
                globValue = "[0..9]*"
            }
            assertEquals(GlobConstraint, constraint.metaClass)
            assertEquals("test_glob", constraint.name)
            assertEquals("some description", constraint.description)
            assertEquals("[0..9]*", constraint.globValue)
            assertEquals(
                """
           |Constraint Glob "test_glob" {
           |  description = "some description"
           |  globValue = "[0..9]*"
           |}
           """.trimMargin(),
                constraint.toPrettyString()
            )
        }
    }

    @Test
    fun `creation of enum constraint`() {
        container {
            val constraint = enumConstraint("test_enum") {
                literal("a") {
                    description = "some description of a"
                }
                literal("b") {
                    description = "some description of b"
                }
            }
            assertEquals(EnumConstraint, constraint.metaClass)
            assertEquals("test_enum", constraint.name)
            assertEquals("some description of a", constraint.attributes[0].description)
            assertEquals("some description of b", constraint.attributes[1].description)
            assertEquals(
                """
           |Constraint Enum "test_enum" {
           |  a
           |  b
           |}
           """.trimMargin(),
                constraint.toPrettyString()
            )
        }
    }
}
