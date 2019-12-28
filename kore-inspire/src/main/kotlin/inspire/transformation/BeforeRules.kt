@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

/**
 * Patch: eaxmiid41 is a UML:DataType with name <undefined>
 */
val `remove references to undefined Data Type`: Transform = { _, _ ->
    patch<KoreTypedElement>(predicate = { type?.id == "eaxmiid41" }) { type = null }
}

/**
 * Patch: add dataType refinement to PT_Locale (4F7072DC_5423_4978_8EA2_1DE43135931B)
 */
val `add Data Type tag to PT_Locale`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { id == "4F7072DC_5423_4978_8EA2_1DE43135931B" }) {
        findOrCreateAnnotation().references.add(koreClass { name = Stereotypes.dataType })
    }
}

/**
 * Patch: add dataType refinement to LocalisedCharacterString (AE1AC547_B120_4488_A63F_60A8A7441D7A)
 */
val `add Data Type tag to LocalisedCharacterString`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { id == "AE1AC547_B120_4488_A63F_60A8A7441D7A" }) {
        findOrCreateAnnotation().references.add(koreClass { name = Stereotypes.dataType })
    }
}

/**
 * Patch: add dataType refinement to Identifier (CB20C133_5AA4_4671_80C7_8ED2879AB0D9)
 */
val `add Data Type tag to Identifier`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { id == "CB20C133_5AA4_4671_80C7_8ED2879AB0D9" }) {
        findOrCreateAnnotation().references.add(koreClass { name = Stereotypes.dataType })
    }
}

/**
 * Patch: fix typo in edgeMatched default value
 */
val `standardize edgeMatched default value`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = { defaultValueLiteral == "edge-matched" }) {
        defaultValueLiteral = "edgeMatched"
    }
}

/**
 * Patch: fix typo in CodeList
 */
val `standardize codeList`: Transform = fixStrereotype(Stereotypes.codeList)

/**
 * Patch: fix typo in Union
 */
val `standardize union`: Transform = fixStrereotype(Stereotypes.union)

/**
 * Patch: fix typo in FeatureType
 */
val `standardize featureType`: Transform = fixStrereotype(Stereotypes.featureType)

/**
 * Patch: fix typo in DataType
 */
val `standardize dataType`: Transform = fixStrereotype(Stereotypes.dataType)

/**
 * Patch add missing xmlns
 */
val `add missing xmlns`: Transform = { _, _ ->
    patch<KoreNamedElement>( predicate = { name == "Units of Measure" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "gml")
    }
    patch<KoreNamedElement>( predicate = { name == "ISO 19136 GML" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "gml")
    }
    patch<KoreNamedElement>( predicate = { name == "Quadrilateral Grid" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "cis")
    }
}

fun fixStrereotype(value: String): Transform = { _, _ ->
    patch<KoreClass>(predicate = {
        getAnnotation()
            ?.references
            ?.filterIsInstance<KoreNamedElement>()
            ?.any {  value.equals(it.name, true) }
            ?: false
    }) {
        getAnnotation()
            ?.references
            ?.filterIsInstance<KoreNamedElement>()
            ?.filter { value.equals(it.name, true) }
            ?.forEach { it.name = value }
    }
}


val `Before rules`: List<Transform> = listOf(
    `remove references to undefined Data Type`,
    `add Data Type tag to PT_Locale`,
    `add Data Type tag to LocalisedCharacterString`,
    `add Data Type tag to Identifier`,
    `standardize edgeMatched default value`,
    `standardize codeList`,
    `standardize union`,
    `standardize featureType`,
    `standardize dataType`,
    `add missing xmlns`
)

