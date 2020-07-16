/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreObject
import es.iaaa.kore.KoreReference
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Replace abstract types in properties by identifier`: Transform = { conversion, _ ->

    val identifier by lazy {
        conversion.model.allContent().filterIsInstance<KoreClass>()
            .find { it.id == "CB20C133_5AA4_4671_80C7_8ED2879AB0D9" }
            ?: throw Exception("Unexpected error")
    }

    patch<KoreReference>(predicate = {
        type.isFeatureType() && type.isAbstract() && name != null
    }) {
        name = name + "_" + type?.name
        type = identifier
    }
}

fun KoreObject?.isFeatureType() = if (this is KoreClass) references(Stereotypes.featureType) else false

fun KoreObject?.isAbstract() = if (this is KoreClass) isAbstract else false
