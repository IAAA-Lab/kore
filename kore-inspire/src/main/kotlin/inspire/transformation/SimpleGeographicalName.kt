@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Simple Geographical Name`: Transform = { conversion, _ ->

    val baseTypes2 by lazy {
        conversion.model.allContent().filterIsInstance<KorePackage>().find { it.name == "Base Types 2" }
            ?: throw Exception("Essential package 'Base Types 2' not found")
    }

    val geographicalNames by lazy {
        conversion.model.allContent().filterIsInstance<KorePackage>().find { it.name == "Geographical Names" }
            ?: throw Exception("Essential package 'Geographical Names' not found")
    }

    val characterString by lazy {
        conversion.model.allContent().filterIsInstance<KoreClassifier>().find { it.name == "CharacterString" }
            ?: throw Exception("Essential type 'CharacterString' not found")
    }

    val simpleGeographicalName by lazy {
        koreClass {
            name = "SimpleGeographicalName"
            attribute { name = "spelling_text"; type = characterString; lowerBound = 1 }
            attribute { name = "language"; type = characterString }
            container = baseTypes2
            stereotype(Stereotypes.dataType)
        }
    }

    patch<KoreAttribute>(predicate = { type?.name == "GeographicalName" && applicationSchema()?.name != geographicalNames.name }) {
        type = simpleGeographicalName
    }
}

fun KoreObject?.applicationSchema(): KorePackage? =
    when {
        this == null -> null
        this is KorePackage -> if (references(Stereotypes.applicationSchema)) this else container.applicationSchema()
        else -> container.applicationSchema()
    }
