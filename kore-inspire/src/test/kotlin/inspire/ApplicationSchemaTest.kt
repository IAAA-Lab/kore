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

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KorePackage
import es.iaaa.kore.findDefaultNamedReferences
import es.iaaa.kore.resource.ResourceFactory
import es.iaaa.kore.resource.ResourceHelper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class ApplicationSchemaTest {

    companion object {
        private const val INSPIRE_CONSOLIDATED_UML_MODEL =
            "INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml"
        lateinit var model: KorePackage

        @Suppress("unused")
        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            val file = File("src/main/resources/$INSPIRE_CONSOLIDATED_UML_MODEL")
            val resource = ResourceFactory.createResource(file, "EA-UML1.3", ResourceHelper())
            if (resource.errors.size > 0) {
                println("Errors:")
                resource.errors.forEach { println(it) }
            }
            if (resource.warnings.size > 0) {
                println("Warnings:")
                resource.warnings.forEach { println(it) }
            }
            model = resource.contents.filterIsInstance<KorePackage>()[0]
        }
    }

    @Test
    fun `locate application schema AdministrativeUnits`() {
        with(INSPIRE) {
            val au = model.schema("AdministrativeUnits")
            assertNotNull(au)
        }
    }

    @Test
    fun `locate feature type AdministrativeBoundary`() {
        with(INSPIRE) {
            val au = model.schema("AdministrativeUnits") as KorePackage
            val administrativeBoundary = au.findClassifier("AdministrativeBoundary") as KoreClass
            assertNotNull(administrativeBoundary)
            with(administrativeBoundary) {
                assertTrue(isFeatureType())
                properties {
                    "country" type "CountryCode"
                    "geometry" type "GM_Curve"
                    "inspireId" type "Identifier"
                    "nationalLevel" type "AdministrativeHierarchyLevel" cardinality 1..6
                    "beginLifespanVersion" type "DateTime" stereotype "voidable" and "lifeCycleInfo"
                    "endLifespanVersion" type "DateTime" cardinality AtMostOne stereotype "voidable" and "lifeCycleInfo"
                    "legalStatus" type "LegalStatusValue" initialValue "agreed" stereotype "voidable"
                    "technicalStatus" type "TechnicalStatusValue" initialValue "edge-matched" stereotype "voidable"
                    "admUnit" type "AdministrativeUnit" cardinality AtLeastOne stereotype "voidable"
                }
            }
        }
    }

    @Test
    fun `locate feature type AdministrativeUnit`() {
        with(INSPIRE) {
            val au = model.schema("AdministrativeUnits") as KorePackage
            val administrativeUnit = au.findClassifier("AdministrativeUnit") as KoreClass
            assertNotNull(administrativeUnit)
            with(administrativeUnit) {
                assertTrue(isFeatureType())
                properties {
                    "country" type "CountryCode"
                    "geometry" type "GM_MultiSurface"
                    "inspireId" type "Identifier"
                    "name" type "GeographicalName" cardinality AtLeastOne
                    "nationalCode" type "CharacterString"
                    "nationalLevel" type "AdministrativeHierarchyLevel"
                    "beginLifespanVersion" type "DateTime" stereotype "voidable" and "lifeCycleInfo"
                    "endLifespanVersion" type "DateTime" cardinality AtMostOne stereotype "voidable" and "lifeCycleInfo"
                    "nationalLevelName" type "LocalisedCharacterString" cardinality AtLeastOne stereotype "voidable"
                    "residenceOfAuthority" type "ResidenceOfAuthority" cardinality AtLeastOne stereotype "voidable"
                    "boundary" type "AdministrativeBoundary" cardinality AtLeastOne stereotype "voidable"
                    "condominium" type "Condominium" cardinality Unrestricted stereotype "voidable"
                    "coAdminister" type "AdministrativeUnit" cardinality Unrestricted stereotype "voidable"
                    "administeredBy" type "AdministrativeUnit" cardinality Unrestricted stereotype "voidable"
                    "upperLevelUnit" type "AdministrativeUnit" cardinality AtMostOne stereotype "voidable"
                    "lowerLevelUnit" type "AdministrativeUnit" cardinality Unrestricted stereotype "voidable"
                }
            }
        }
    }

    @Test
    fun `locate feature type Condominium`() {
        with(INSPIRE) {
            val au = model.schema("AdministrativeUnits") as KorePackage
            val condominium = au.findClassifier("Condominium") as KoreClass
            assertNotNull(condominium)
            with(condominium) {
                assertTrue(isFeatureType())
                properties {
                    "geometry" type "GM_MultiSurface"
                    "inspireId" type "Identifier"
                    "beginLifespanVersion" type "DateTime" stereotype "voidable" and "lifeCycleInfo"
                    "endLifespanVersion" type "DateTime" cardinality AtMostOne stereotype "voidable" and "lifeCycleInfo"
                    "name" type "GeographicalName" cardinality Unrestricted stereotype "voidable"
                    "admUnit" type "AdministrativeUnit" cardinality AtLeastOne stereotype "voidable"
                }
            }
        }
    }

    @Test
    fun `locate data type ResidenceOfAuthority`() {
        with(INSPIRE) {
            val au = model.schema("AdministrativeUnits") as KorePackage
            val residenceOfAuthority = au.findClassifier("ResidenceOfAuthority") as KoreClass
            assertNotNull(residenceOfAuthority)
            with(residenceOfAuthority) {
                assertTrue(isDataType())
                properties {
                    "name" type "GeographicalName"
                    "geometry" type "GM_Point" stereotype "voidable"
                }
            }
        }
    }

    @Test
    fun `locate enumeration LegalStatusValue`() {
        with(INSPIRE) {
            val au = model.schema("AdministrativeUnits") as KorePackage
            val legalStatusValue = au.findClassifier("LegalStatusValue") as KoreClass
            assertNotNull(legalStatusValue)
            assertTrue(legalStatusValue.isEnumeration())
            assertTrue(legalStatusValue.superTypes.isEmpty())
            assertEquals(listOf("agreed", "notAgreed"), legalStatusValue.values())
        }
    }

    @Test
    fun `locate enumeration TechnicalStatusValue`() {
        with(INSPIRE) {
            val au = model.schema("AdministrativeUnits") as KorePackage
            val technicalStatusValue = au.findClassifier("TechnicalStatusValue") as KoreClass
            assertNotNull(technicalStatusValue)
            assertTrue(technicalStatusValue.superTypes.isEmpty())
            assertEquals(listOf("edgeMatched", "notEdgeMatched"), technicalStatusValue.values())
        }
    }

    @Test
    fun `locate code list AdministrativeHierarchyLevel`() {
        with(INSPIRE) {
            val au = model.schema("AdministrativeUnits") as KorePackage
            val administrativeHierarchyLevel = au.findClassifier("AdministrativeHierarchyLevel") as KoreClass
            assertNotNull(administrativeHierarchyLevel)
            assertTrue(administrativeHierarchyLevel.isCodeList())
            assertTrue(administrativeHierarchyLevel.superTypes.isEmpty())
            assertEquals(
                listOf("1stOrder", "2ndOrder", "3rdOrder", "4thOrder", "5thOrder", "6thOrder"),
                administrativeHierarchyLevel.values()
            )
        }
    }
}

object INSPIRE {

    private fun KorePackage.schemas(): List<KorePackage> = allSubpackages().filter { it.isAppSchema() }.toList()

    fun KorePackage.schema(name: String): KorePackage? = schemas().find { it.name == name }

    private fun KorePackage.isAppSchema(): Boolean = findDefaultNamedReferences().any { it.name == "applicationSchema" }

    fun KoreClass.isFeatureType(): Boolean = findDefaultNamedReferences().any { it.name == "featureType" }

    fun KoreClass.isCodeList(): Boolean = findDefaultNamedReferences().any { it.name == "codeList" }

    fun KoreClass.isEnumeration(): Boolean = findDefaultNamedReferences().any { it.name == "enumeration" }

    fun KoreClass.isDataType(): Boolean = findDefaultNamedReferences().any { it.name == "dataType" }

    fun KoreClass.values(): List<String> =
        if (isEnumeration() || isCodeList()) attributes.mapNotNull { it.name } else emptyList()
}

class ClassInfoChecker {
    private val checks = mutableListOf<CheckData>()

    data class CheckData(
        var name: String,
        var type: String,
        var cardinality: Multiplicity = ExactlyOne,
        var stereotypes: List<String> = emptyList(),
        var initialValue: String? = null
    )

    infix fun String.type(type: String): CheckData = CheckData(
        name = this,
        type = type
    ).also { checks.add(it) }

    infix fun CheckData.cardinality(cardinality: Multiplicity): CheckData {
        this.cardinality = cardinality
        return this
    }

    infix fun CheckData.cardinality(cardinality: IntRange): CheckData {
        this.cardinality = Multiplicity(cardinality.first, cardinality.last)
        return this
    }

    infix fun CheckData.stereotype(stereotype: String): CheckData {
        this.stereotypes = listOf(stereotype)
        return this
    }

    infix fun CheckData.and(stereotype: String): CheckData {
        this.stereotypes += stereotype
        return this
    }

    infix fun CheckData.initialValue(initialValue: String): CheckData {
        this.initialValue = initialValue
        return this
    }

    fun runChecks(classInfo: KoreClass) {
        assertEquals(checks.size, classInfo.allStructuralFeatures().filter { it.name != null }.size)
        checks.forEach {
            val property = classInfo.findStructuralFeature(it.name) ?: fail { "Property ${it.name} not found" }
            assertEquals(it.type, property.type?.name, "In property ${it.name}")
            assertEquals(it.cardinality.minOccurs, property.lowerBound, "In property ${it.name}")
            assertEquals(it.cardinality.maxOccurs, property.upperBound, "In property ${it.name}")
            assertEquals(it.initialValue, property.defaultValueLiteral)
            it.stereotypes.forEach { tag ->
                assertTrue(property.findDefaultNamedReferences().find { ref -> ref.name == tag } != null,
                    "In property ${it.name} not found $tag")
            }
        }
    }
}

fun KoreClass.properties(block: ClassInfoChecker.() -> Unit) {
    val checker = ClassInfoChecker()
    checker.apply(block)
    checker.runChecks(this)
}

open class Multiplicity(
    val minOccurs: Int = 1,
    val maxOccurs: Int = 1
) {
    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other === this -> true
        other is Multiplicity ->
            minOccurs == other.minOccurs && maxOccurs == other.maxOccurs
        else -> false
    }

    override fun toString(): String {
        return "[$minOccurs..${if (maxOccurs == -1) "*" else maxOccurs.toString()}]"
    }

    override fun hashCode(): Int {
        var result = minOccurs
        result = 31 * result + maxOccurs
        return result
    }
}

object AtMostOne : Multiplicity(0, 1)
object AtLeastOne : Multiplicity(1, -1)
object Unrestricted : Multiplicity(0, -1)
object ExactlyOne : Multiplicity(1, 1)
