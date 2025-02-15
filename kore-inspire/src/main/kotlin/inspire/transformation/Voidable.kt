/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KorePackage
import es.iaaa.kore.models.gpkg.metadata
import es.iaaa.kore.models.gpkg.mimeType
import es.iaaa.kore.models.gpkg.scope
import es.iaaa.kore.models.gpkg.standardUri
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.setLowerBoundWhen

/**
 * If a property has exactly a cardinality of 1 but has the stereotype <<voidable>>,
 * it is mapped to a column that allows null values.
 */
val `Voidable mapped as nullable`: Transform = { _, _ ->
    setLowerBoundWhen(0) { it.references(Stereotypes.voidable) }
}

val `load authoritative descriptions of the reasons for void values as metadata`: Transform = { conversion, _ ->

    val definitions = listOf(
        Triple(1, "Unknown", "attribute"),
        Triple(2, "Unpopulated", "attributeType"),
        Triple(3, "Withheld", "attribute"),
        Triple(4, "Withheld", "attributeType")
    )
    val rootContainer by lazy {
        conversion.model.allContent().filterIsInstance<KorePackage>().first {
            it.references(Stereotypes.applicationSchema) && "Base Types" == it.name
        }
    }

    patch<KoreClass>(predicate = { name == "VoidReasonValue" }, global = true) {
        val parentName = name
        attributes.forEach { att ->
            definitions.filter { it.second == att.name }.forEach { def ->
                val voidValueReason = rootContainer.metadata {
                    id = def.first.toString()
                    name = def.second
                    scope = def.third
                    standardUri = "http://www.isotc211.org/2005/gmd"
                    mimeType = "text/plain"
                    metadata = "http://inspire.ec.europa.eu/codelist/$parentName/${def.second}"
                }
                conversion.track(voidValueReason)
            }
        }
    }
}

val Voidable: List<Transform> = listOf(
    `Voidable mapped as nullable`,
    `load authoritative descriptions of the reasons for void values as metadata`
)