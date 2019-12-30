@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.toReference
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Convert potential references`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = { type?.metaClass == AttributesTable }) {
        toReference()
    }
    patch<KoreClass>(predicate = { attributes.any { it.upperBound != 1 } }) {
        attributes.filter { it.upperBound != 1 }.forEach { it.toReference() }
    }
}