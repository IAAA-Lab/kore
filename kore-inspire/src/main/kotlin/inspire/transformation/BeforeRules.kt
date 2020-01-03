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
        addStereotype(Stereotypes.dataType)
    }
}

/**
 * Patch: add dataType refinement to LocalisedCharacterString (AE1AC547_B120_4488_A63F_60A8A7441D7A)
 */
val `add Data Type tag to LocalisedCharacterString`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { id == "AE1AC547_B120_4488_A63F_60A8A7441D7A" }) {
        addStereotype(Stereotypes.dataType)
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
val `standardize codeList`: Transform = fixStereotype(Stereotypes.codeList)

/**
 * Patch: fix typo in Union
 */
val `standardize union`: Transform = fixStereotype(Stereotypes.union)

/**
 * Patch: fix typo in FeatureType
 */
val `standardize featureType`: Transform = fixStereotype(Stereotypes.featureType)

/**
 * Patch: fix typo in DataType
 */
val `standardize dataType`: Transform = fixStereotype(Stereotypes.dataType)

/**
 * Patch: fix typo in enumeration
 */
val `standardize enumeration`: Transform = fixStereotype(Stereotypes.enumeration)

/**
 * Patch: fix typo in types
 */
val `standardize type`: Transform = fixStereotype(Stereotypes.type)

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
    // TODO Review
    patch<KoreNamedElement>( predicate = { name == "ISO 19156:2011 Observations and Measurements" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "")
    }
    // TODO Review
    patch<KoreNamedElement>( predicate = { name == "ISO 19133 Tracking and Navigation" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "")
    }
    // TODO Review
    patch<KoreNamedElement>( predicate = { name == "ISO 19110 Methodology for feature cataloguing" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "")
    }
    // TODO Review
    patch<KoreNamedElement>( predicate = { name == "ISO 19115-2:2009 Metadata - Imagery" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "")
    }
    // TODO Review
    patch<KoreNamedElement>( predicate = { name == "ISO 19119 Services" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "")
    }
    // TODO Review
    patch<KoreNamedElement>( predicate = { name == "ISO 19115:2006 Metadata (Corrigendum)" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "gmd")
    }
    // TODO Review
    patch<KoreNamedElement>( predicate = { name == "ISO 19103:2005 Schema Language" }, global = true ) {
        getAnnotation()?.details?.put("xmlns", "")
    }

}

fun fixStereotype(value: String): Transform = { _, _ ->
    patch<KoreModelElement>(predicate = {
        getAnnotation()
            ?.references
            ?.filterIsInstance<KoreNamedElement>()
            ?.any {  value.equals(it.name, true) }
            ?: false
    }, global = true) {
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
    `standardize edgeMatched default value`,
    `standardize codeList`,
    `standardize union`,
    `standardize featureType`,
    `standardize dataType`,
    `standardize type`,
    `standardize enumeration`,
    `add missing xmlns`
)


fun KoreModelElement.addStereotype(stereotype: String) = findOrCreateAnnotation().references.add(koreClass { name = stereotype })