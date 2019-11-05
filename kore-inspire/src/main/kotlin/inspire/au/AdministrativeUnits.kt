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
package inspire.au

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.conversion
import es.iaaa.kore.transform.impl.Console
import es.iaaa.kore.transform.impl.GeoPackageWriter
import es.iaaa.kore.transform.rules.*
import java.net.URL

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
            /**
             * Patch: eaxmiid41 is a UML:DataType with name <undefined>
             */
            patch<KoreTypedElement>(predicate = { type?.id == "eaxmiid41" }) { type = null }

            /**
             * Patch: add dataType refinement to PT_Locale (EAID_4F7072DC_5423_4978_8EA2_1DE43135931B)
             */
            patch<KoreClass>(predicate = { id == "EAID_4F7072DC_5423_4978_8EA2_1DE43135931B" }) {
                findOrCreateAnnotation().references.add(KoreModel.createClass().apply { name = "dataType" })
            }

            /**
             * Patch: add dataType refinement to LocalisedCharacterString (EAID_AE1AC547_B120_4488_A63F_60A8A7441D7A)
             */
            patch<KoreClass>(predicate = { id == "EAID_AE1AC547_B120_4488_A63F_60A8A7441D7A" }) {
                findOrCreateAnnotation().references.add(KoreModel.createClass().apply { name = "dataType" })
            }

            patch<KoreAttribute>(predicate = { defaultValueLiteral == "edge-matched" }) {
                defaultValueLiteral = "edgeMatched"
            }

            patch<KoreClass>(predicate = hasRefinement("CodeList")) {
                getAnnotation()?.references?.filterIsInstance<KoreNamedElement>()
                    ?.filter { it.name == "CodeList" }
                    ?.forEach { it.name = "codeList" }
            }

            patch<KoreClass>(predicate = { id == "EAID_AE1AC547_B120_4488_A63F_60A8A7441D7A" }) {
                findOrCreateAnnotation().references.add(KoreModel.createClass().apply { name = "dataType" })
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

            /**
             * Patch: convert inspireId attribute o references
             */
            patch<KoreClass>(predicate = { attributes.any { it.name == "inspireId" } }) {
                attributes.filter { it.name == "inspireId" }.forEach { it.toReference() }
            }

            rule(`general rule ISO-19103 Basic Types`)
            rule(`general rule ISO-19107 Geometry Types`)
            rule(`general rule ISO-19139 Metadata XML Implementation Types`)

            rule(`flatten Data Types with upper cardinality of 1`)

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
            patch<KoreClass>(predicate = { references.isNotEmpty() }) {
                references.forEach {
                    val base = it.containingClass
                    val related = it.type
                    val baseName = it.name
                    val relatedName = it.opposite?.name
                    if (base != null && related != null && baseName != null &&
                        (base.metaClass in listOf(AttributesTable, FeaturesTable)) &&
                        (related.metaClass in listOf(AttributesTable, FeaturesTable))
                    ) {

                        if (it.isMany && it.opposite?.isMany == true) {
                            val container = base.container
                            val tableName = prefixes.getOrDefault(
                                container?.name,
                                ""
                            ) + if (relatedName != null) baseName + "_" + relatedName else base.name + "_" + baseName

                            val relationProfile = if (related.metaClass == AttributesTable) "attributes" else "features"

                            container?.relation(tableName) {
                                profile = relationProfile
                                reference {
                                    name = "base_id"; columnName = "base_id"; type = base
                                    findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(BaseTable)
                                }
                                reference {
                                    name = "related_id"; columnName = "related_id"; type = related
                                    findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(RelatedTable)
                                }
                            }?.also { rel ->
                                track(rel)
                            }
                            it.containingClass = null
                            it.opposite?.containingClass = null
                        } else if (it.isMany) {
                            val container = base.container
                            val tableName = prefixes.getOrDefault(
                                container?.name,
                                ""
                            ) + if (relatedName != null) baseName + "_" + relatedName else base.name + "_" + baseName
                            val newAttribute = it.opposite?.toAttribute()?.apply {
                                type = IntegerType()
                                lowerBound = 0
                                upperBound = 1
                            }
                            val relationProfile =
                                if (related.metaClass == AttributesTable) "attributes" else "features"
                            container?.relation(tableName) {
                                profile = relationProfile
                                relatedReference = newAttribute
                                reference {
                                    name = "base_id"; columnName = "base_id"; type = base
                                    findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(BaseTable)
                                }
                                reference {
                                    name = "related_id"; columnName = "related_id"; type = related
                                    findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(RelatedTable)
                                }
                            }?.also { rel ->
                                track(rel)
                            }
                            it.containingClass = null
                        } else {
                            val container = base.container
                            val tableName = prefixes.getOrDefault(
                                container?.name,
                                ""
                            ) + if (relatedName != null) baseName + "_" + relatedName else base.name + "_" + baseName

                            val relationProfile =
                                if (related.metaClass == AttributesTable) "attributes" else "features"
                            val newAttribute = it.toAttribute().apply {
                                type = IntegerType()
                                lowerBound = 0
                                upperBound = 1
                            }

                            container?.relation(tableName) {
                                profile = relationProfile
                                relatedReference = newAttribute
                                reference {
                                    name = "base_id"; columnName = "base_id"; type = base
                                    findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(BaseTable)
                                }
                                reference {
                                    name = "related_id"; columnName = "related_id"; type = related
                                    findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(RelatedTable)
                                }
                            }?.also { rel ->
                                track(rel)
                            }
                            it.opposite?.containingClass = null
                        }
                    }
                }
            }

            //
            // Remove dangling references.
            //
            patch<KoreClass>(predicate = { references.isNotEmpty() }) {
                references.filter { it.name == null }.forEach {
                    it.containingClass = null
                    it.opposite?.containingClass = null
                }
            }

            setMetMetaclassWhen(Column, predicate = isColumn())

            forEachAttribute(predicate = { it.metaClass == Column && it.columnName == null }) {
                it.columnName = it.name
            }

            patch<KoreClass>(predicate = { metaClass in listOf(FeaturesTable, AttributesTable) }) {
                findTaggedValue("package_name")?.let {
                    identifier = "$it::$name"
                }
            }

            patch<KoreClass>(predicate = { metaClass in listOf(FeaturesTable, AttributesTable) }) {
                findTaggedValue("documentation")?.let { text ->
                    val tokens = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
                    val idx = tokens.indexOf("-- Name --")
                    description = tokens.mapIndexed { pos, str ->
                        when {
                            str.startsWith("--") -> ""
                            pos == idx + 1 && idx >= 0 -> ""
                            else -> str
                        }
                    }.filter { it.isNotBlank() }.joinToString("\n\n")
                }
            }

            patch<KoreClass>(predicate = { metaClass == FeaturesTable }) {
                tableName = prefixes.getOrDefault(container?.name, "") + name
                name = tableName
            }
            patch<KoreClass>(predicate = { metaClass == AttributesTable }) {
                tableName = prefixes.getOrDefault(container?.name, "") + name
                name = tableName
            }


            patch<KoreClass>(predicate = { metaClass == EnumConstraint }) {
                name = prefixes.getOrDefault(container?.name, "") + name
            }

            addAttributeWhen(idColumn()) { it.metaClass in listOf(FeaturesTable, AttributesTable) }

            patch<KoreClass>(predicate = { metaClass in listOf(FeaturesTable, AttributesTable) }) {
                val owner = this
                val sorted = attributes.sortedWith(GpkgAttributeComparator)
                sorted.forEach { it.containingClass = null }
                sorted.forEach { it.containingClass = owner }
            }


            patch<KoreAttribute>(predicate = { metaClass == Column && title == null }) {
                findTaggedValue("description")?.let { text ->
                    val tokens = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
                    val idx = tokens.indexOf("-- Name --")
                    if (idx >= 0) {
                        title = tokens[idx + 1]
                    }
                }
            }

            patch<KoreAttribute>(predicate = { metaClass == Column && description == null }) {
                findTaggedValue("description")?.let { text ->
                    val tokens = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
                    val idx = tokens.indexOf("-- Name --")
                    description = tokens.mapIndexed { pos, str ->
                        when {
                            str.startsWith("--") -> ""
                            pos == idx + 1 && idx >= 0 -> ""
                            else -> str
                        }
                    }.filter { it.isNotBlank() }.joinToString("\n\n")
                }
            }

            removeTags(
                listOf(
                    "ea_.*", "version", "tpos", "tagged", "style", "status", "phase", "package",
                    "package_name", "date_created", "date_modified", "complexity", "author", "\\\$ea_.*", "gentype",
                    "isSpecification", "stereotype", "batchload", "batchsave", "created", "iscontrolled",
                    "isprotected", "lastloaddate", "lastsavedate", "logxml", "modified", "owner", "packageFlags",
                    "parent", "usedtd", "xmiver", "xmlpath", "documentation", "eventflags", "persistence"
                )
            )
            removeRefinements(
                listOf(
                    "voidable",
                    "lifeCycleInfo",
                    "dataType",
                    "enumeration",
                    "featureType",
                    "codeList",
                    "CodeList"
                )
            )

            var rootContainer: KorePackage? = null

            patch<KorePackage>(predicate = { name == "AdministrativeUnits" }) {
                metaClass = Container
                fileName = name
                model.allRelevantContent().filterIsInstance<KoreClass>().filter {
                    it.metaClass == AttributesTable || it.metaClass == FeaturesTable ||
                        it.metaClass == RelationTable ||
                        it.metaClass == EnumConstraint
                }.forEach {
                    it.container = this
                }
                rootContainer = this
            }

            var globalCounter: Long = 1
            patch<KoreClass>(predicate = { name == "VoidReasonValue" }, global = true) {
                val parentName = name
                attributes.forEach {
                    rootContainer?.metadata {
                        id = globalCounter.toString()
                        name = it.name
                        scope = "dataset"
                        standardUri = "http://inspire.ec.europa.eu/codelist_register/codelist/item"
                        mimeType = "application/xml"
                        val url = "http://inspire.ec.europa.eu/codelist/$parentName/${it.name}/${it.name}.en.xml"
                        metadata = URL(url)
                            .openStream()
                            .use { it.bufferedReader().readText() }
                        globalCounter++
                    }
                }
            }
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

private fun idColumn(): () -> KoreAttribute {
    return {
        column {
            name = "id"; columnName = "id"; title = "Id"; description = "Id"; lowerBound = 1; type =
            IntegerType(); geoPackageSpec().add(PrimaryKey)
        }
    }
}

private fun hasRefinement(name: String, source: String? = null): (KoreObject) -> Boolean = { obj ->
    if (obj is KoreModelElement) {
        obj.getAnnotation(source)?.references?.filterIsInstance<KoreNamedElement>()?.any { it.name == name }
            ?: false
    } else false
}

private fun isColumn(): (KoreObject) -> Boolean = { obj ->
    obj is KoreAttribute && obj.container?.metaClass in listOf(FeaturesTable, AttributesTable)
}

fun main() {
    au.convert()
}

fun schemaName(name: String): (KoreObject) -> Boolean = { it ->
    if (it is KorePackage) {
        it.name == name && hasRefinement("applicationSchema")(it)
    } else {
        false
    }
}

object GpkgAttributeComparator : Comparator<KoreAttribute> {
    override fun compare(o1: KoreAttribute, o2: KoreAttribute): Int {
        val r1 = o1.findDefaultNamedReferences()
        val r2 = o2.findDefaultNamedReferences()
        return when {
            o1.isPrimaryKey() -> -1
            o1.name == null -> 1
            o2.name == null -> -1
            r1.size == r2.size -> 0
            r1.isEmpty() || r2.isEmpty() -> r1.size - r2.size
            else -> r2.size - r1.size
        }
    }
}
