@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `ensure that arrays are treated as references from now`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = { type?.metaClass == AttributesTable }) {
        toReference()
    }
    patch<KoreClass>(predicate = { attributes.any { it.upperBound != 1 } }) {
        attributes.filter { it.upperBound != 1 }.forEach { it.toReference() }
    }
}

val `create supporting Attribute tables for Enumerations and Codelists involved in arrays`: Transform = { _, _ ->
    patch<KoreReference>(predicate = {
        findGeoPackageSpec()?.any { Constraint.isInstance(it) } == true
    }) {
        val from = this
        val constraint = from.geoPackageSpec().first { Constraint.isInstance(it) } as KoreClass
        val target = attributes(constraint.name ?: "<<missing>>") {
            container = constraint.container
            constraint.annotations.map { it.copy(this) }
            attribute {
                name = "value"
                type = from.type
                lowerBound = 1
                geoPackageSpec().addAll(from.geoPackageSpec().filter { Constraint.isInstance(it) })
            }
        }
        from.type = target
        from.geoPackageSpec().removeIf { Constraint.isInstance(it) }
    }
}

/**
 * Property types for properties with a cardinality greater than 1 and a simple property type (e.g. String, Integer,
 * Float, ...) may use arrays of these simple types.
 *
 * Property types for properties with a cardinality greater than 1 and an enumeration or codelist are implemented
 * similar to N:M association roles.
 */
val Arrays: List<Transform> = listOf(
    `ensure that arrays are treated as references from now`,
    `create supporting Attribute tables for Enumerations and Codelists involved in arrays`
)
