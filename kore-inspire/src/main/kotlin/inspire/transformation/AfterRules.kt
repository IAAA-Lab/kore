@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.KorePackage
import es.iaaa.kore.findTaggedValue
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.addAttributeWhen
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.removeTags

val `add geopackage primary column`: Transform = { _, _ ->
    addAttributeWhen({ it.idColumn() }) { it.metaClass in listOf(FeaturesTable, AttributesTable) }
}

val `ensure column metaclass`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { metaClass in listOf(FeaturesTable, AttributesTable) }) {
        allAttributes().forEach {
            it.metaClass = Column
            if (it.name.isNullOrBlank()) {
                it.name = "value"
            }
            it.columnName = it.name
        }
    }
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
            description = processText(text)
        }
    }
}

val `copy documentation to table description`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { hasTable() }) {
        findTaggedValue("documentation")?.let { text ->
            description = processText(text)
        }
    }
}

private fun processText(text: String): String {
    val tokens = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
    val idx = tokens.indexOf("-- Name --")
    return tokens.mapIndexed { pos, str ->
        when {
            str.startsWith("--") -> ""
            pos == idx + 1 && idx >= 0 -> ""
            else -> str
        }
    }.filter { it.isNotBlank() }.joinToString("\n\n")
}

val `mark containers`: Transform = { conversion, _ ->

    val potentialItems by lazy {
        conversion.model.allRelevantContent().filterIsInstance<KoreClass>().filter { it.isStorable() }
    }

    patch<KorePackage>(predicate = { classifiers.intersect(potentialItems).isNotEmpty() }, global = true) {
        metaClass = Container
        fileName = name
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
            "memberIdentifierStem", "object_style", "gmlMixin", "isnamespace", "modeldocument"
        )
    )
}

val `audit`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = {
        !GpkgDataType.isInstance(type) &&
                metaClass != EnumConstraintValue
    }) {
        //        println("${containingClass?.container?.name} ${containingClass?.name} $name ${type?.name} ${type?.id}")
    }
}

private fun KoreClass?.isStorable(): Boolean =
    isFeaturesTable() || isAttributesTable() || isRelationTable() || isEnumConstraint()

val `After rules`: List<Transform> = listOf(
    `add geopackage primary column`,
    `ensure column metaclass`,
    `copy documentation to column description`,
    `copy documentation to table description`,
    `mark containers`,
    `remove dangling references`,
    `remove unused tags`,
    `audit`
)
