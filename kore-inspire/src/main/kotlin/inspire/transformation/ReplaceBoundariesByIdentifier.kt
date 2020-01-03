package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Replace boundaries by Identifier`: Transform = { conversion, _ ->

    val identifier by lazy {
        conversion.model.allContent().filterIsInstance<KoreClass>().find { it.id == "CB20C133_5AA4_4671_80C7_8ED2879AB0D9" } ?: throw Exception("Unexpected error")
    }

    patch<KoreAttribute> {
        val typeIsFeature = type?.references(Stereotypes.featureType) == true
        val typeIsOutside = type?.container !in conversion.model.selectedPackages()

        if (typeIsFeature && typeIsOutside) {
            val prefix = name ?: "<<missing>>"
            val postfix = type?.name ?: "<<missing>>"
            name = "${prefix}_$postfix"
            type = identifier
        }
    }

    patch<KoreReference> {
        val typeIsFeature = type?.references(Stereotypes.featureType) == true
        val typeIsOutside = type?.container !in conversion.model.selectedPackages()

        if (typeIsFeature && typeIsOutside && isNavigable) {
            val prefix = name ?: "<<missing>>"
            val postfix = type?.name ?: "<<missing>>"
            name = "${prefix}_$postfix"
            type = identifier
            toAttribute()
        }
    }

}
