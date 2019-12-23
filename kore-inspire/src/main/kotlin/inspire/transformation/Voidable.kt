@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KorePackage
import es.iaaa.kore.findDefaultNamedReferences
import es.iaaa.kore.models.gpkg.metadata
import es.iaaa.kore.models.gpkg.mimeType
import es.iaaa.kore.models.gpkg.scope
import es.iaaa.kore.models.gpkg.standardUri
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.setLowerBoundWhen
import java.net.URL

/**
 * If a property has exactly a cardinality of 1 but has the stereotype <<voidable>>,
 * it is mapped to a column that allows null values.
 */
val `Voidable mapped as nullable`: Transform = { _, _ ->
    setLowerBoundWhen(0) { it.findDefaultNamedReferences().any { ref -> ref.name == "voidable" } }
}

val `load authoritative descriptions of the reasons for void values as metadata`: Transform = { conversion, options ->
    val withMetadata = options["metadata"] == true
    val withSelector = conversion.input.selector.get()
    val definitions = listOf(
        Triple(1, "Unknown", "attribute"),
        Triple(2, "Unpopulated", "attributeType"),
        Triple(3, "Withheld", "attribute"),
        Triple(4, "Withheld", "attributeType")
    )

    var rootContainer: KorePackage? = null

    patch(predicate = withSelector) {
        rootContainer = this
    }

    patch<KoreClass>(predicate = { name == "VoidReasonValue" }, global = true) {
        val parentName = name
        attributes.forEach { att ->
            definitions.filter { it.second == att.name }.forEach { def ->
                rootContainer?.metadata {
                    id = def.first.toString()
                    name = def.second
                    scope = def.third
                    standardUri = "http://www.isotc211.org/2005/gmd"
                    mimeType = "text/xml"
                    val url =
                        "http://inspire.ec.europa.eu/codelist/$parentName/${def.second}/${def.second}.en.iso19135xml"
                    metadata = if (withMetadata) URL(url).openStream().use { it.bufferedReader().readText() } else ""
                }
            }
        }
    }
}

val Voidable: List<Transform> = listOf(
    `Voidable mapped as nullable`,
    `load authoritative descriptions of the reasons for void values as metadata`
)