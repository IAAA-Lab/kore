@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.hasNoReferences
import es.iaaa.kore.nameIs
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.flattenTypes

/**
 * This encoding rule is applied to all DataType types that are used as value type by the property x of a other type
 * but the data type Identifier.
 */
val `Flattening types`: Transform = { _, _ ->
    flattenTypes(predicate = { obj ->
        (obj.references("dataType") || obj.hasNoReferences()) && !(obj nameIs "Identifier")
    },
        postFlatten = { old, new ->
            new.name = "${old.name}_${new.name}"
            new.lowerBound = kotlin.math.min(old.lowerBound, new.lowerBound)
        })
}