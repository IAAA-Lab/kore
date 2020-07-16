/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package inspire

import es.iaaa.kore.KoreObject
import es.iaaa.kore.KorePackage
import es.iaaa.kore.references
import es.iaaa.kore.transform.Conversion

fun main() {
    val config = Configuration(
        file = "kore-inspire/src/main/resources/INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml",
        description = true,
        sql = true
    )
    mapOf(
        // "au" to "AdministrativeUnits",
        // "mu" to "MaritimeUnits",
        // "gn" to "Geographical Names"
        // "am" to "Controlled Activities",
        // "hy" to "Hydro - Physical Waters",
        // "lc" to "LandCoverVector"
        // "lu" to "Existing Land Use"
        // "tn" to "Road Transport Network",
        "ge" to "Geology"
    ).map {
        configuration(it.value, it.key, config).convert()
    }
}

fun configuration(schema: String, output: String, config: Configuration): Conversion {
    val options = mapOf(
        "description" to config.description,
        "name" to output,
        "sql" to config.sql,
        "metadata" to config.metadata
    )
    return transformation(options).apply {
        input.selector.set(schemaName(schema))
        input.file.set(config.file)
        context.putAll(options)
    }
}

fun schemaName(name: String): (KoreObject) -> Boolean = {
    when (it) {
        is KorePackage -> it.name == name && it.references("applicationSchema")
        else -> false
    }
}

data class Configuration(
    val file: String,
    val description: Boolean = true,
    val sql: Boolean = true,
    val metadata: Boolean = true
)