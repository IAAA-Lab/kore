@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.TextType
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.mapEntry
import kotlin.math.min

/**
 * URI is encoded as TEXT.
 */
val `ISO 19139 type - URI`: Transform = { _, _ ->
    mapEntry(type = "URI", targetType = TextType())
}

/**
 * LocalisedCharacterString locale is added as a separate property.
 */
val `ISO 19139 type - LocalisedCharacterString`: Transform = { _, _ ->
    patch<KoreClass>(predicate = {
        name == "LocalisedCharacterString"
    }) {
        attribute {
            name = "text"
            type = TextType()
            lowerBound = 1
        }
    }
    patch<KoreReference>(predicate = { type?.name == "PT_Locale" && upperBound == 1 }) {
        toAttribute()
    }
    patch<KoreAttribute>(predicate = { type?.name == "PT_Locale" && upperBound == 1 }) {
        val parent = containingClass!!
        val atts = parent.attributes
        atts.forEach { it.containingClass = null }
        atts.forEach { att ->
            if (att == this) {
                (att.type as KoreClass).allAttributes().forEach { toBeCopiedAtt ->
                    val addedAtt = toBeCopiedAtt.copy(parent)
                    addedAtt.name = toBeCopiedAtt.name
                    addedAtt.lowerBound = min(toBeCopiedAtt.lowerBound, att.lowerBound)
                }
            } else {
                att.containingClass = parent
            }
        }
    }
}

val `ISO 19139 - Metadata XML Implementation Types`: List<Transform> = listOf(
    `ISO 19139 type - LocalisedCharacterString`,
    `ISO 19139 type - URI`
)

