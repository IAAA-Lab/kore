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
package inspire

import es.iaaa.kore.*
import es.iaaa.kore.resource.ResourceFactory
import es.iaaa.kore.util.toPrettyString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class InputUmlTest {

    @Test
    fun loadedRootModel() {
        assertEquals(1, models.size)
        assertEquals("EA Model", models[0].name)
        assertEquals("854E3882_3E39_4a93_A34F_DA6FBF474080", models[0].id)
        assertEquals(256, models[0].classifiers.size)
        assertEquals(1, models[0].subpackages.size)
        assertEquals(3270, models[0].allClassifiers().count())
        assertEquals(541, models[0].allSubpackages().count())
        println(models[0].toPrettyString())
    }

    @Test
    fun loadedInspireModel() {
        val base = models[0]
        val pkg = base.allSubpackages().find { it.name == "INSPIRE Consolidated UML Model" }
        if (pkg != null) {
            assertEquals(14, pkg.annotations[0].details.size)
            assertEquals("0", pkg.annotations[0].details["batchload"])
            assertEquals(0, pkg.classifiers.size)
            assertEquals(3, pkg.subpackages.size)
            assertEquals(3014, pkg.allClassifiers().count())
            assertEquals(540, pkg.allSubpackages().count())
        } else fail()
    }

    @Test
    fun loadClass() {
        val base = models[0]
        val cls = base.allClassifiers().find { it.name == "TimeLocationValueTriple" }
        assertTrue(cls is KoreClass)
        cls as KoreClass
        assertFalse(cls.isAbstract)
        assertFalse(cls.isInterface)
        assertEquals(1, cls.findDefaultNamedReferences().count { it.name == "dataType" })
        assertEquals(21, cls.annotations[0].details.size)
        assertEquals(1, cls.attributes.size)
        assertEquals("location", cls.attributes[0].name)
        assertEquals(false, cls.attributes[0].isChangeable)
        assertEquals(false, cls.attributes[0].isUnsettable)
        assertEquals(1, cls.attributes[0].lowerBound)
        assertEquals(1, cls.attributes[0].upperBound)
        assertEquals("ECC8BD1C_7D88_457a_AF27_7A56AAA88506", cls.attributes[0].type?.id)
    }

    @Test
    fun checkStereotypeFix() {
        val base = models[0]
        val pkg = base.allSubpackages().find { it.name == "Observable Properties" }
        val stereo =
            pkg?.findDefaultNamedReferences()?.find { it.id == "9624C906_96BD_4af7_A34A_D59C7B072E90" } ?: fail()
        assertEquals("applicationSchema", stereo.name)
    }

    @Test
    fun checkDefaultValue() {
        val base = models[0]
        val feature = base.allContents()
            .filterIsInstance<KoreStructuralFeature>()
            .find { it.name == "four" } ?: fail()
        assertEquals("4", feature.defaultValueLiteral)
    }

    @Test
    fun checkOperation() {
        val base = models[0]
        val operation = base.allContents()
            .filterIsInstance<KoreOperation>()
            .find { it.name == "unregisterID" } ?: fail()
        assertEquals("NameSpace", operation.containingClass?.name)
    }

    @Test
    fun checkSupertypes() {
        val base = models[0]
        val cls = base.allContents()
            .filterIsInstance<KoreClass>()
            .find { it.name == "EnvironmentalMonitoringNetwork" }
        assertNotNull(cls)
        cls as KoreClass
        assertEquals(1, cls.superTypes.size)
        assertEquals(2, cls.allSuperTypes().size)
        assertEquals(2, cls.structuralFeatures.size)
        assertEquals(19, cls.allStructuralFeatures().size)
    }

    companion object {
        private const val INSPIRE_CONSOLIDATED_UML_MODEL =
            "INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml"
        var models: List<KorePackage> = emptyList()

        @Suppress("unused")
        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            val file = File("src/main/resources/$INSPIRE_CONSOLIDATED_UML_MODEL")
            val resource = ResourceFactory.createResource(file, "EA-UML1.3", emptyMap())
            if (resource.errors.size > 0) {
                println("Errors:")
                resource.errors.forEach { println(it) }
            }
            if (resource.warnings.size > 0) {
                println("Warnings:")
                resource.warnings.forEach { println(it) }
            }
            models = resource.contents.filterIsInstance<KorePackage>()
        }
    }
}
