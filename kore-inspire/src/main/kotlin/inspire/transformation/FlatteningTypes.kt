/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.copy
import es.iaaa.kore.nameIs
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.flattenTypes

/**
 * This encoding rule is applied to all DataType types that are used as value type by the property x of a other type
 * but the data type Identifier.
 */
val `Flattening types`: Transform = { _, _ ->
    flattenTypes(predicate = canFlatten(), debugPredicate = { obj ->
        "name=${obj.name} id=${obj.id} canFlatten=${canFlatten()(obj)}"
    }, postFlatten = { old, new ->
        new.name = if (new.name.isNullOrEmpty()) old.name else "${old.name}_${new.name}"
        new.lowerBound = kotlin.math.min(old.lowerBound, new.lowerBound)
        old.annotations.forEach { it.copy(new) }
    })
}

private fun canFlatten(): (KoreNamedElement) -> Boolean = { obj ->
    !(obj.references(Stereotypes.union) ||
            obj.references(Stereotypes.codeList) ||
            obj.references(Stereotypes.enumeration) ||
            obj nameIs "Identifier")
}