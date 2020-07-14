/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.resource

import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KoreTypedElement
import es.iaaa.kore.resource.impl.EnterpriseArchitectUml13Factory
import java.io.File

/**
 * A registry of resource factories.
 */
object ResourceFactory {
    val factories: MutableMap<String, Factory> = mutableMapOf(
        "EA-UML1.3" to EnterpriseArchitectUml13Factory
    )

    fun createResource(file: File, format: String, helper: ResourceHelper = ResourceHelper()): Resource {
        return factories[format]?.createResource(file, helper) ?: throw IllegalArgumentException("Unknown $format")
    }
}

/**
 * Common interface for factories.
 */
interface Factory {
    fun createResource(file: File, resourceHelper: ResourceHelper): Resource
}

data class ResourceHelper(
    val alias: Map<String, String> = emptyMap(),
    val selectType: (KoreTypedElement, List<KoreClassifier>) -> Pair<KoreClassifier?, List<String>> = { _, _ ->
        Pair(
            null,
            emptyList()
        )
    }
)