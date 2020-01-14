@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.KoreTypedElement.Companion.UNBOUNDED_MULTIPLICITY
import es.iaaa.kore.models.gpkg.TextType
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Simple Localised Character String`: Transform = { conversion, _ ->

    val baseTypes2 by lazy {
        conversion.model.allContent().filterIsInstance<KorePackage>().find { it.name == "Base Types 2" }
            ?: throw Exception("Essential package 'Base Types 2' not found")
    }

    val locale by lazy {
        koreClass {
            name = "Locale"
            container = baseTypes2
            stereotype(Stereotypes.codeList)
        }
    }

    val simpleLocalisedCharacterString by lazy {
        koreClass {
            name = "SimpleLocalisedCharacterString"
            attribute { name = ""; lowerBound = 1; type = TextType() }
            attribute { name = "locale"; lowerBound = 1; type = locale }
            container = baseTypes2
            stereotype(Stereotypes.type)
        }
    }

    patch<KoreTypedElement>(predicate = { type?.name == "LocalisedCharacterString" }, global = true) {
        type = simpleLocalisedCharacterString
    }

    patch<KoreTypedElement>(predicate = { type?.name == "PT_FreeText" }, global = true) {
        type = simpleLocalisedCharacterString
        upperBound = UNBOUNDED_MULTIPLICITY
    }
}
