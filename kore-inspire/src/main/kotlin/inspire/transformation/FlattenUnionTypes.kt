@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.copy
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

/**
 * Properties whose type is an union type and have a maximum multiplicity of 1 are flattened.
 *
 * A union type is structured data type without identity where exactly one of the properties of the type is present
 * in any instance. The property to be flattened is replaced by a copy of all the properties of the union type.
 *
 * The name of the each new property is its original name in the union type prepended of the name of the
 * replaced property plus an underscore ('_'). The minimum multiplicity of the added properties is 0.
 * The remaining characteristics of the replaced property are copied to the new properties.
 *
 * The restriction that exactly one of the properties of the type is present in any instance may be
 * may be enforced by code in applications that update GeoPackage data values.
 */
val `Flatten union types`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = { upperBound == 1 && type?.references("Union") == true }) {
        val union = type as? KoreClass ?: throw Exception("Union types must be classes")
        union.attributes.forEach { attribute ->
            copy(containingClass as KoreClass).apply {
                name = "${name}_${attribute.name}"
                type = attribute.type as? KoreClass ?: throw Exception("Attributes of union types must be classes")
                lowerBound = 0
            }
        }
        containingClass = null
    }
}
