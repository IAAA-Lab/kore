@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.findTaggedValue
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.addAttributeWhen
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.removeRefinements
import es.iaaa.kore.transform.rules.removeTags

val `add geopackage primary column`: Transform = { _, _ ->
    addAttributeWhen({ IdColumn() }) { it.metaClass in listOf(FeaturesTable, AttributesTable) }
}

val `copy documentation to column description`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = { metaClass == Column && title == null }) {
        findTaggedValue("description")?.let { text ->
            val tokens = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
            val idx = tokens.indexOf("-- Name --")
            if (idx >= 0) {
                title = tokens[idx + 1]
            }
        }
    }

    patch<KoreAttribute>(predicate = { metaClass == Column && description == null }) {
        findTaggedValue("description")?.let { text ->
            val tokens = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
            val idx = tokens.indexOf("-- Name --")
            description = tokens.mapIndexed { pos, str ->
                when {
                    str.startsWith("--") -> ""
                    pos == idx + 1 && idx >= 0 -> ""
                    else -> str
                }
            }.filter { it.isNotBlank() }.joinToString("\n\n")
        }
    }
}

val `copy documentation to table description`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { hasTable() }) {
        findTaggedValue("documentation")?.let { text ->
            val tokens = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
            val idx = tokens.indexOf("-- Name --")
            description = tokens.mapIndexed { pos, str ->
                when {
                    str.startsWith("--") -> ""
                    pos == idx + 1 && idx >= 0 -> ""
                    else -> str
                }
            }.filter { it.isNotBlank() }.joinToString("\n\n")
        }
    }
}

val `move to the selected package`: Transform = { conversion, _ ->
    patch(predicate = conversion.input.selector.get()) {
        metaClass = Container
        fileName = name
        conversion.model.allRelevantContent()
            .filterIsInstance<KoreClass>()
            .filter { it.isMoveable() }
            .forEach { it.container = this }
    }
}

val `remove dangling references`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { references.isNotEmpty() }) {
        references.filter { it.name == null }.forEach {
            it.containingClass = null
            it.opposite?.containingClass = null
        }
    }
}

/**
 * Cleanup: remove unused tags.
 */
val `remove unused tags`: Transform = { _, _ ->
    removeTags(
        listOf(
            "ea_.*", "version", "tpos", "tagged", "style", "status", "phase", "package",
            "package_name", "date_created", "date_modified", "complexity", "author", "\\\$ea_.*", "gentype",
            "isSpecification", "stereotype", "batchload", "batchsave", "created", "iscontrolled",
            "isprotected", "lastloaddate", "lastsavedate", "logxml", "modified", "owner", "packageFlags",
            "parent", "usedtd", "xmiver", "xmlpath", "documentation", "eventflags", "persistence",
            "vocabulary", "extendableByMS", "asDictionary", "codeList", "gmlProfileSchema", "xmlns", "xsdDocument",
            "targetNamespace", "xsdEncodingRule", "byValuePropertyType", "isCollection", "noPropertyType",
            "xsdEncodingRule", "extensibility", "inspireConcept", "codeSpace", "dictionaryIdentifier",
            "memberIdentifierStem", "object_style", "gmlMixin"
        )
    )
}

/**
 * Cleanup: remove stereotypes.
 */
val `remove stereotypes`: Transform = { _, _ ->
    removeRefinements(
        listOf(
            "voidable",
            "lifeCycleInfo",
            "dataType",
            "enumeration",
            "featureType",
            "codeList"
        )
    )
}

private fun KoreClass?.isMoveable(): Boolean =
    isFeaturesTable() || isAttributesTable() || isRelationTable() || isEnumConstraint()

val `After rules`: List<Transform> = listOf(
    `add geopackage primary column`,
    `copy documentation to column description`,
    `copy documentation to table description`,
    `move to the selected package`,
    `remove dangling references`,
    `remove unused tags`,
    `remove stereotypes`
)
