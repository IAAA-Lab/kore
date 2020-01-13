package inspire

import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.KoreObject
import es.iaaa.kore.findTaggedValue
import es.iaaa.kore.resource.ResourceHelper


val inspireResourceHelper = ResourceHelper(
    alias = mapOf(
        //       "eaxmiid25" to "2CF3B75E_8B1F_4853_B0B4_10C65AD71551", // Distance
        //       "eaxmiid189" to "AE1AC547_B120_4488_A63F_60A8A7441D7A", // LocalisedCharacterString
        //       "eaxmiid190" to "CB20C133_5AA4_4671_80C7_8ED2879AB0D9", // Identifier
        //       "eaxmiid191" to "E548F6CD_653D_4dc7_AAF3_2E510C1453E0", // GeographicalName
        //       "eaxmiid197" to "F8A23D50_CB8F_4c91_BD7F_C2082467D81A"  // PT_FreeText
    ),
    selectType = { target, candidates ->
        val noNames = candidates.map { it.fullName }.distinct() == listOf(null)
        if (noNames) {
            Pair(null, emptyList())
        } else {
            val xsdEncodingRule = target.xsdEncodingRule().normalize()
            val iso19136 = candidates.filter { it.findParent("ISO 19136 GML") }
            val iso19103 = candidates.filter { it.findParent("ISO 19103:2005 Schema Language") }
            val iso19115 = candidates.filter { it.findParent("ISO 19115:2006 Metadata (Corrigendum)") }
            val govserv =
                candidates.filter { it.findParent("AdministrativeAndSocialGovernmentalServices") && !it.findParent("ExtensionAdministrativeAndSocialGovernmentalServices") }
            if ("iso19136_2007" == xsdEncodingRule && iso19136.size == 1) {
                Pair(iso19136[0], emptyList())
            } else if (iso19103.size == 1) {
                Pair(iso19103[0], emptyList())
            } else if (iso19115.size == 1) {
                Pair(iso19115[0], emptyList())
            } else if (govserv.size == 1) {
                Pair(govserv[0], emptyList())
            } else {
                val msg = candidates.map { it.fullName }.distinct().joinToString()
                val warnings = listOf(
                    "### ${target.fullName} has an unresolved type\n" +
                            "Resolves to ${candidates.size} multiples candidates with names $msg\n" +
                            (target.xsdEncodingRule().normalize() ?: "")
                )
                Pair(null, warnings)
            }
        }
    }
)

private fun KoreObject?.xsdEncodingRule(): String? =
    if (this is KoreModelElement) findTaggedValue("xsdEncodingRule") ?: container.xsdEncodingRule()
    else null

private fun KoreObject?.findParent(name: String): Boolean =
    when (this) {
        is KoreNamedElement -> if (this.name == name) true else container.findParent(name)
        is KoreObject -> container.findParent(name)
        else -> false
    }

private fun String?.normalize(): String? =
    when {
        this == null -> null
        this.startsWith("iso19136_2007") -> "iso19136_2007"
        this.startsWith("iso 19136_2007") -> "iso19136_2007"
        else -> throw Exception(this)
    }
