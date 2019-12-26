@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreObject
import es.iaaa.kore.KorePackage
import es.iaaa.kore.findTaggedValue
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `add qualified name to prefixable elements`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { isPrefixable() }) {
        findTaggedValue("package_name")?.let {
            identifier = "$it::$name"
        }
    }
}

val `default package prefixes`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { isPrefixable() }) {
        val term = if (isRelationTable()) tableName else name
        requireNotNull(term) {
            "Error found in a U2G class: we expect here a not null value in '${if (isRelationTable()) "tableName" else "name"}'"
        }
        val prefixedTerm = runCatching { lookupPrefix() }.getOrElse { assignPrefix() } + term
        if (hasTable()) {
            tableName = prefixedTerm
        }
        name = prefixedTerm
    }
}

/**
 * The prefix is the first occurrence of the tagged value xmlns in the hierarchy.
 */
private fun KoreObject?.lookupPrefix(): String = when (this) {
    is KorePackage -> findTaggedValue("xmlns")
        ?.takeWhile { it != '#' }
        ?.toUpperCase()
        ?.replace("-", "_")
        ?.plus("_")
        ?: container.lookupPrefix()
    is KoreObject -> container.lookupPrefix()
    else -> throw Exception("Not computable U2G prefix: No xmlns tag found in the hierarchy")
}

private fun KoreClass?.isPrefixable(): Boolean = hasTable() || isEnumConstraint()

private fun KoreClass.assignPrefix(): String = when (container?.name) {
    "Cultural and linguistic adapdability" -> "GMD_"           // ISO 19139 freeText.xsd
    "ISO 00639 Language Codes" -> "GMD_"                       // ISO 19139 freeText.xsd
    "ISO 03166 Country Codes" -> "GMD_"                        // ISO 19139 freeText.xsd
    "Identification information" -> "GMD_"                     // ISO 19139 identification.xsd
    "Citation and responsible party information" -> "GMD_"     // ISO 19139 citation.xsd
    "Data quality information" -> "GMD_"                       // ISO 19139 dataQuality.xsd
    "Units of Measure" -> "UM_"                                // TODO Discover why this is required by Evlevation - vector elemsnts
    "Temporal Reference System" -> "TRS_"
    "Maintenance information" -> "MI_"
    "Metadata extension information" -> "MEI_"
    "Distribution information" -> "DI_"
    "Data quality evaluation" -> "DQE_"
    "Enumerations" -> "GCO_"
    "Geometric primitive" -> ""
    "Segmented Curve" -> "SC_"
    "Coverage Core" -> "CC_"
    null -> throw Exception("Not computable U2G prefix: No xmlns tag found in the hierarchy for package without name")
    else -> throw Exception("Not computable U2G prefix: No xmlns tag found in the hierarchy for package '${container?.name}'")
}

val `Naming modification`: List<Transform> = listOf(
    `add qualified name to prefixable elements`,
    `default package prefixes`
)
