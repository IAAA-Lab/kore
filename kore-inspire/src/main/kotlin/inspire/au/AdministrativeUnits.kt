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
@file:Suppress("ObjectPropertyName")

package inspire.au

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KoreModel
import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.KorePackage
import es.iaaa.kore.KoreReference
import es.iaaa.kore.KoreTypedElement
import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.models.gpkg.BaseTable
import es.iaaa.kore.models.gpkg.EnumConstraint
import es.iaaa.kore.models.gpkg.FeaturesTable
import es.iaaa.kore.models.gpkg.GeoPackageSpec
import es.iaaa.kore.models.gpkg.IntegerType
import es.iaaa.kore.models.gpkg.RelatedTable
import es.iaaa.kore.models.gpkg.RelationTable
import es.iaaa.kore.models.gpkg.columnName
import es.iaaa.kore.models.gpkg.profile
import es.iaaa.kore.models.gpkg.reference
import es.iaaa.kore.models.gpkg.relatedReference
import es.iaaa.kore.models.gpkg.relation
import es.iaaa.kore.models.gpkg.tableName
import es.iaaa.kore.toAttribute
import es.iaaa.kore.transform.conversion
import es.iaaa.kore.transform.impl.Console
import es.iaaa.kore.transform.impl.GeoPackageWriter
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.removeRefinements
import es.iaaa.kore.transform.rules.removeTags

/**
 * Patch: eaxmiid41 is a UML:DataType with name <undefined>
 */
val `remove references to undefined Data Type`: Transform = { _, _ ->
    patch<KoreTypedElement>(predicate = { type?.id == "eaxmiid41" }) { type = null }
}

/**
 * Patch: add dataType refinement to PT_Locale (EAID_4F7072DC_5423_4978_8EA2_1DE43135931B)
 */
val `add Data Type tag to PT_Locale`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { id == "EAID_4F7072DC_5423_4978_8EA2_1DE43135931B" }) {
        findOrCreateAnnotation().references.add(KoreModel.createClass().apply { name = "dataType" })
    }
}

/**
 * Patch: add dataType refinement to LocalisedCharacterString (EAID_AE1AC547_B120_4488_A63F_60A8A7441D7A)
 */
val `add Data Type tag to LocalisedCharacterString`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { id == "EAID_AE1AC547_B120_4488_A63F_60A8A7441D7A" }) {
        findOrCreateAnnotation().references.add(KoreModel.createClass().apply { name = "dataType" })
    }
}

/**
 * Patch: add dataType refinement to Identifier (EAID_CB20C133_5AA4_4671_80C7_8ED2879AB0D9)
 */
val `add Data Type tag to Identifier`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { id == "EAID_CB20C133_5AA4_4671_80C7_8ED2879AB0D9" }) {
        findOrCreateAnnotation().references.add(KoreModel.createClass().apply { name = "dataType" })
    }
}

/**
 * Patch: fix typo in edgeMatched default value
 */
val `standardize edgeMatched default value`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = { defaultValueLiteral == "edge-matched" }) {
        defaultValueLiteral = "edgeMatched"
    }
}

/**
 * Patch: fix typo in CodeList
 */
val `standardize codeList`: Transform = { _, _ ->
    patch<KoreClass>(predicate = {
        getAnnotation()
            ?.references
            ?.filterIsInstance<KoreNamedElement>()
            ?.any { it.name == "CodeList" }
            ?: false
    }) {
        getAnnotation()
            ?.references
            ?.filterIsInstance<KoreNamedElement>()
            ?.filter { it.name == "CodeList" }
            ?.forEach { it.name = "codeList" }
    }
}

/**
 * Cleanup: remove unused tags.
 */
val `remove unused tags`: Transform = { _, _ ->
    removeTags(
        listOf(
            "ea_.*", "version", "tpos", "tagged", "style", "status", "phase", "package",
            "package_name", "date_created", "date_modified", "complexity", "author", "\\\$ea_.*", "gentype",
            "isSpecification", "stereotype", "batchload", "batchsave", "created", "iscontrolled",
            "isprotected", "lastloaddate", "lastsavedate", "logxml", "modified", "owner", "packageFlags",
            "parent", "usedtd", "xmiver", "xmlpath", "documentation", "eventflags", "persistence"
        )
    )
}

/**
 * Cleanup: remove stereotypes.
 */
val `remove stereotypes`: Transform = { _, _ ->
    removeRefinements(
        listOf(
            "voidable",
            "lifeCycleInfo",
            "dataType",
            "enumeration",
            "featureType",
            "codeList"
        )
    )
}

val prefixes = mapOf(
    "Base Types 2" to "BASE2_",
    "Base Types" to "BASE_",
    "Cultural and linguistic adapdability" to "GMD_",
    "AdministrativeUnits" to "AU_",
    "Geographical Names" to "GN_",
    "ISO 00639 Language Codes" to "GMD_",
    "ISO 03166 Country Codes" to "GMD_"
)

val au =
    conversion {
        input {
            type.set("EA-UML1.3")
            file.set("src/main/resources/INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml")
            selector.set(schemaName("AdministrativeUnits"))
            alias.set(
                mapOf(
                    "eaxmiid189" to "EAID_AE1AC547_B120_4488_A63F_60A8A7441D7A", // LocalisedCharacterString
                    "eaxmiid190" to "EAID_CB20C133_5AA4_4671_80C7_8ED2879AB0D9", // Identifier
                    "eaxmiid197" to "EAID_F8A23D50_CB8F_4c91_BD7F_C2082467D81A"  // PT_FreeText
                )
            )
        }
        transformation {
            manipulation(`remove references to undefined Data Type`)
            manipulation(`add Data Type tag to PT_Locale`)
            manipulation(`add Data Type tag to LocalisedCharacterString`)
            manipulation(`add Data Type tag to Identifier`)
            manipulation(`standardize edgeMatched default value`)
            manipulation(`standardize codeList`)

            rule(`general rule ISO-19103 Basic Types`)
            rule(`general rule ISO-19107 Geometry Types`)
            rule(`general rule ISO-19139 Metadata XML Implementation Types`)

            rule(`flatten Data Types with upper cardinality of 1 but Identifier`)

            rule(`simple Feature Type stereotype to GeoPackage Feature`)
            rule(`Feature Type stereotype without geometry to GeoPackage Attribute`)
            rule(`Data Type stereotype to GeoPackage Attribute`)
            rule(`simple feature-like Data Type stereotype to GeoPackage Feature`)

            rule(`general rule Enumeration Types`, mapOf("description" to false))
            rule(`general rule CodeList Types`, mapOf("description" to false))
            rule(`voidable properties have a min cardinality of 0`)

            manipulation(`ensure that arrays are treated as references from now`)

            rule(`create supporting Attribute tables for Enumerations and Codelists involved in arrays`)

            /**
             * Patch: create relation table from references with inverse of different types
             */
            patch<KoreReference>(
                predicate = {
                    name != null &&
                        containingClass?.metaClass in listOf(AttributesTable, FeaturesTable) &&
                        type?.metaClass in listOf(AttributesTable, FeaturesTable)
                }
            ) {
                val tableName = "${containingClass?.name}_$name"
                val relationProfile = if (type?.metaClass == AttributesTable) "attributes" else "features"

                val manyToOne = isMany && opposite?.isMany != true
                val oneToOne = !isMany

                toRelation(tableName, relationProfile)?.let {
                    it.relatedReference = when {
                        manyToOne -> opposite?.copyAsRefAttribute()
                        oneToOne -> copyAsRefAttribute()
                        else -> null
                    }
                    track(it)
                }

                containingClass = null
                opposite?.containingClass = null
            }

            rule(`properties with maximum cardinality 1 to columns`)
            rule(`load authoritative descriptions of the reasons for void values as metadata`)

            manipulation(`add qualified name to features and attributes`)

            rule(`default package prefixes`, mapOf("prefixes" to prefixes))

            manipulation(`add geopackage primary column`)
            manipulation(`copy documentation to column description`)
            manipulation(`copy documentation to table description`)
            manipulation(`move to the selected package`)
            manipulation(`remove dangling references`)
            manipulation(`remove unused tags`)
            manipulation(`remove stereotypes`)
        }
        output {
            add(Console)
            add(
                GeoPackageWriter(
                    overwrite = true,
                    base = "templates"
                )
            )
        }
    }

fun main() {
    au.convert()
}

private fun KoreReference?.copyAsRefAttribute(): KoreAttribute? = this?.toAttribute(remove = false)?.apply {
    type = IntegerType()
    lowerBound = 0
    upperBound = 1
}

private fun KoreReference.toRelation(
    tableName: String,
    relationProfile: String
): KoreClass? = type?.let { t ->
    containingClass?.let {
        val pkg = it.container
        pkg?.toRelation(tableName, relationProfile, it, t)
    }
}

private fun KorePackage.toRelation(
    tableName: String,
    relationProfile: String,
    base: KoreClass,
    related: KoreClassifier
): KoreClass = relation(tableName) {
    profile = relationProfile
    reference {
        name = "base_id"
        columnName = "base_id"
        type = base
        findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(BaseTable)
    }
    reference {
        name = "related_id"
        columnName = "related_id"
        type = related
        findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(RelatedTable)
    }
}
