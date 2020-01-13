@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Simple Citation`: Transform = { conversion, _ ->

    val baseTypes2 by lazy {
        conversion.model.allContent().filterIsInstance<KorePackage>().find { it.name == "Base Types 2" }
            ?: throw Exception("Essential package 'Base Types 2' not found")
    }

    val characterString by lazy {
        conversion.model.allContent().filterIsInstance<KoreClassifier>().find { it.name == "CharacterString" }
            ?: throw Exception("Essential type 'CharacterString' not found")
    }

    val date by lazy {
        conversion.model.allContent().filterIsInstance<KoreClassifier>().find { it.name == "Date" }
            ?: throw Exception("Essential type 'Date' not found")
    }

    val url by lazy {
        conversion.model.allContent().filterIsInstance<KoreClassifier>().find { it.name == "URL" }
            ?: throw Exception("Essential type 'URL' not found")
    }

    val legislationLevelValue by lazy {
        conversion.model.allContent().filterIsInstance<KoreClassifier>().find { it.name == "LegislationLevelValue" }
            ?: throw Exception("Essential type 'URL' not found")
    }

    val simpleCitationType by lazy {
        koreClass {
            name = "SimpleCitationType"
            attribute { name = "CI_Citation"; lowerBound = 1 }
            attribute { name = "LegislationCitation"; lowerBound = 1 }
            attribute { name = "DocumentCitation"; lowerBound = 1 }
            container = baseTypes2
            stereotype(Stereotypes.enumeration)
        }
    }

    val simpleCitation by lazy {
        koreClass {
            name = "SimpleCitation"
            attribute { name = "name"; type = characterString; lowerBound = 1 }
            attribute { name = "type"; type = simpleCitationType; lowerBound = 1 }
            attribute { name = "level"; type = legislationLevelValue }
            attribute { name = "date"; type = date }
            attribute { name = "link"; type = url }
            container = baseTypes2
            stereotype(Stereotypes.dataType)
        }
    }

    patch<KoreAttribute>(predicate = {
        type?.name in listOf(
            "CI_Citation",
            "LegislationCitation",
            "DocumentCitation"
        )
    }) {
        type = simpleCitation
    }
}
