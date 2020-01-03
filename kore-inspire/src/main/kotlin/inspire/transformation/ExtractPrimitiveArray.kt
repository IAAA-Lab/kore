@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch
import org.atteo.evo.inflector.English
import java.lang.Exception

/**
 * Property types for properties with a cardinality greater than 1 and a simple property type (e.g. String, Integer,
 * Float, ...) may use arrays of these simple types.
 */
val `Extract primitive array`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = { upperBound != 1 && canBeEncodedAsText(type) }) {
        geoPackageSpec().removeAll { Constraint.isInstance(it) }
        type = TextType()
        upperBound = 1
        val segments = (name as String).split("_")
        name = (segments.dropLast(1) + English.plural(segments.last())).joinToString(separator = "_")
        addStereotype(Stereotypes.array)
    }
}

fun canBeEncodedAsText(type: KoreClassifier?): Boolean = when {
    type == null -> false
    GpkgDataType.isInstance(type) -> if (GeometryType.isInstance(type)) false else (!BlobType.isInstance(type))
    else -> false
}