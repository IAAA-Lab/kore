@file:Suppress("ObjectPropertyName")

package inspire.au

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.KoreObject
import es.iaaa.kore.KorePackage
import es.iaaa.kore.KoreReference
import es.iaaa.kore.copy
import es.iaaa.kore.findDefaultNamedReferences
import es.iaaa.kore.findTaggedValue
import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.models.gpkg.BooleanType
import es.iaaa.kore.models.gpkg.Column
import es.iaaa.kore.models.gpkg.Constraint
import es.iaaa.kore.models.gpkg.Container
import es.iaaa.kore.models.gpkg.CurveType
import es.iaaa.kore.models.gpkg.DateTimeType
import es.iaaa.kore.models.gpkg.DateType
import es.iaaa.kore.models.gpkg.DoubleType
import es.iaaa.kore.models.gpkg.EnumConstraint
import es.iaaa.kore.models.gpkg.EnumConstraintValue
import es.iaaa.kore.models.gpkg.FeaturesTable
import es.iaaa.kore.models.gpkg.GeometryType
import es.iaaa.kore.models.gpkg.IntegerType
import es.iaaa.kore.models.gpkg.MultiSurfaceType
import es.iaaa.kore.models.gpkg.PointType
import es.iaaa.kore.models.gpkg.PrimaryKey
import es.iaaa.kore.models.gpkg.RelationTable
import es.iaaa.kore.models.gpkg.TextType
import es.iaaa.kore.models.gpkg.attribute
import es.iaaa.kore.models.gpkg.attributes
import es.iaaa.kore.models.gpkg.column
import es.iaaa.kore.models.gpkg.columnName
import es.iaaa.kore.models.gpkg.description
import es.iaaa.kore.models.gpkg.fileName
import es.iaaa.kore.models.gpkg.findGeoPackageSpec
import es.iaaa.kore.models.gpkg.geoPackageSpec
import es.iaaa.kore.models.gpkg.identifier
import es.iaaa.kore.models.gpkg.metadata
import es.iaaa.kore.models.gpkg.mimeType
import es.iaaa.kore.models.gpkg.scope
import es.iaaa.kore.models.gpkg.standardUri
import es.iaaa.kore.models.gpkg.title
import es.iaaa.kore.toAttribute
import es.iaaa.kore.toReference
import es.iaaa.kore.transform.Conversion
import es.iaaa.kore.transform.Transformations
import es.iaaa.kore.transform.rules.addAttributeWhen
import es.iaaa.kore.transform.rules.flattenTypes
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.setLowerBoundWhen
import es.iaaa.kore.transform.rules.setMetMetaclassWhen
import es.iaaa.kore.transform.rules.setTypeWhen
import java.net.URL

typealias Transform = Transformations.(Conversion, Map<String, Any>) -> Unit

val `simple Feature Type stereotype to GeoPackage Feature`: Transform = { _, _ ->
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature("featureType"))
}

val `Feature Type stereotype without geometry to GeoPackage Attribute`: Transform = { _, _ ->
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute("featureType"))
}

val `Data Type stereotype to GeoPackage Attribute`: Transform = { _, _ ->
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature("dataType"))
}

val `simple feature-like Data Type stereotype to GeoPackage Feature`: Transform = { _, _ ->
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute("dataType"))
}

val `general rule ISO-19103 Basic Types`: Transform = { _, _ ->
    setTypeWhen(TextType(), predicate = { it.type?.name == "CharacterString" })
    setTypeWhen(BooleanType(), predicate = { it.type?.name == "Boolean" })
    setTypeWhen(IntegerType(), predicate = { it.type?.name == "Integer" })
    setTypeWhen(DoubleType(), predicate = { it.type?.name == "Real" })
    setTypeWhen(TextType(), predicate = { it.type?.name == "Decimal" })
    setTypeWhen(DateTimeType(), predicate = { it.type?.name == "DateTime" })
    setTypeWhen(DateType(), predicate = { it.type?.name == "Date" })
}

val `general rule ISO-19107 Geometry Types`: Transform = { _, _ ->
    setTypeWhen(CurveType(), predicate = { it.type?.name == "GM_Curve" })
    setTypeWhen(MultiSurfaceType(), predicate = { it.type?.name == "GM_MultiSurface" })
    setTypeWhen(PointType(), predicate = { it.type?.name == "GM_Point" })
}

val `URI subrule`: Transform = { _, _ ->
    setTypeWhen(TextType(), predicate = { it.type?.name == "URI" })
}

val `LocalisedCharacterString subrule`: Transform = { _, _ ->
    patch<KoreClass>(predicate = {
        name == "LocalisedCharacterString"
    }) {
        attribute {
            name = "text"
            type = TextType()
            lowerBound = 1
        }
    }
    patch<KoreReference>(predicate = { type?.name == "PT_Locale" && upperBound == 1 }) {
        toAttribute()
    }
    patch<KoreAttribute>(predicate = { type?.name == "PT_Locale" && upperBound == 1 }) {
        val parent = containingClass!!
        val atts = parent.attributes
        atts.forEach { it.containingClass = null }
        atts.forEach { att ->
            if (att == this) {
                (att.type as KoreClass).allAttributes().forEach { toBeCopiedAtt ->
                    val addedAtt = toBeCopiedAtt.copy(parent)
                    addedAtt.name = toBeCopiedAtt.name
                    addedAtt.lowerBound = kotlin.math.min(toBeCopiedAtt.lowerBound, att.lowerBound)
                }
            } else {
                att.containingClass = parent
            }
        }
    }
}

val `general rule ISO-19139 Metadata XML Implementation Types`: List<Transform> = listOf(
    `LocalisedCharacterString subrule`,
    `URI subrule`
)

val `general rule Enumeration Types`: Transform = { _, options ->
    val withDescription = options["description"] == true
    val patched = mutableListOf<KoreClass>()
    patch<KoreClass>(predicate = hasRefinement("enumeration")) {
        metaClass = EnumConstraint
        attributes.forEach {
            it.metaClass = EnumConstraintValue
            if (withDescription) {
                val uri = "http://inspire.ec.europa.eu/enumeration/" + it.containingClass?.name + "/" + it.name
                if (khttp.get(uri).statusCode == 200) {
                    it.description = uri
                }
            }
        }
        patched.add(this)
    }
    setTypeWhen(TextType(), predicate = {
        it.type in patched
    }, preset = {
        val type = it.type
        if (type is KoreClass) {
            it.geoPackageSpec().add(type)
        }
    })
}

val `general rule CodeList Types`: Transform = { _, options ->
    val withDescription = options["description"] == true
    val patched = mutableListOf<KoreClass>()
    patch<KoreClass>(predicate = hasRefinement("codeList")) {
        metaClass = EnumConstraint
        attributes.forEach {
            it.metaClass = EnumConstraintValue
            if (withDescription) {
                val uri = "http://inspire.ec.europa.eu/codelist/" + it.containingClass?.name + "/" + it.name
                if (khttp.get(uri).statusCode == 200) {
                    it.description = uri
                }
            }
        }
        patched.add(this)
    }
    setTypeWhen(TextType(), predicate = {
        it.type in patched
    }, preset = {
        val type = it.type
        if (type is KoreClass) {
            it.geoPackageSpec().add(type)
        }
    })
}

val `voidable properties have a min cardinality of 0`: Transform = { _, _ ->
    setLowerBoundWhen(0) { it.findDefaultNamedReferences().any { ref -> ref.name == "voidable" } }
}

val `flatten Data Types with upper cardinality of 1 but Identifier`: Transform = { _, _ ->
    flattenTypes(predicate = { obj -> obj.hasRefinement("dataType") && !obj.hasName("Identifier") },
        postFlatten = { old, new ->
            new.name = "${old.name}_${new.name}"
            new.lowerBound = kotlin.math.min(old.lowerBound, new.lowerBound)
        })
}

val `ensure that arrays are treated as references from now`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = { type?.metaClass == AttributesTable }) {
        toReference()
    }
    patch<KoreClass>(predicate = { attributes.any { it.upperBound != 1 } }) {
        attributes.filter { it.upperBound != 1 }.forEach { it.toReference() }
    }
}

val `create supporting Attribute tables for Enumerations and Codelists involved in arrays`: Transform = { _, _ ->
    patch<KoreReference>(predicate = {
        findGeoPackageSpec()?.any { Constraint.isInstance(it) } == true
    }) {
        val from = this
        val constraint = from.geoPackageSpec().first { Constraint.isInstance(it) } as KoreClass
        val target = attributes(constraint.name ?: "<<missing>>") {
            container = constraint.container
            constraint.annotations.map { it.copy(this) }
            attribute {
                name = "value"
                type = from.type
                lowerBound = 1
                geoPackageSpec().addAll(from.geoPackageSpec().filter { Constraint.isInstance(it) })
            }
        }
        from.type = target
        from.geoPackageSpec().removeIf { Constraint.isInstance(it) }
    }
}

val `load authoritative descriptions of the reasons for void values as metadata`: Transform = { conversion, _ ->
    val withSelector = conversion.input.selector.get()
    val definitions = listOf(
        Triple(1, "Unknown", "attribute"),
        Triple(2, "Unpopulated", "attributeType"),
        Triple(3, "Withheld", "attribute"),
        Triple(4, "Withheld", "attributeType")
    )

    var rootContainer: KorePackage? = null

    patch(predicate = withSelector) {
        rootContainer = this
    }

    patch<KoreClass>(predicate = { name == "VoidReasonValue" }, global = true) {
        val parentName = name
        attributes.forEach { att ->
            definitions.filter { it.second == att.name }.forEach { def ->
                rootContainer?.metadata {
                    id = def.first.toString()
                    name = def.second
                    scope = def.third
                    standardUri = "http://www.isotc211.org/2005/gmd"
                    mimeType = "text/xml"
                    val url =
                        "http://inspire.ec.europa.eu/codelist/$parentName/${def.second}/${def.second}.en.iso19135xml"
                    metadata = URL(url).openStream().use { it.bufferedReader().readText() }
                }
            }
        }
    }
}

val `move to the selected package`: Transform = { conversion, _ ->
    patch(predicate = conversion.input.selector.get()) {
        metaClass = Container
        fileName = name
        conversion.model.allRelevantContent()
            .filterIsInstance<KoreClass>()
            .filter { it.metaClass in listOf(AttributesTable, FeaturesTable, RelationTable, EnumConstraint) }
            .forEach { it.container = this }
    }
}

val `add geopackage primary column`: Transform = { _, _ ->
    addAttributeWhen({
        column {
            name = "id"
            columnName = "id"
            title = "Id"
            description = "Id"
            lowerBound = 1
            type = IntegerType()
            geoPackageSpec().add(PrimaryKey)
        }
    })
    { it.metaClass in listOf(FeaturesTable, AttributesTable) }
}

val `add qualified name to features and attributes`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { metaClass in listOf(FeaturesTable, AttributesTable) }) {
        findTaggedValue("package_name")?.let {
            identifier = "$it::$name"
        }
    }
}

val `copy documentation to table description`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { metaClass in listOf(FeaturesTable, AttributesTable) }) {
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

val `remove dangling references`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { references.isNotEmpty() }) {
        references.filter { it.name == null }.forEach {
            it.containingClass = null
            it.opposite?.containingClass = null
        }
    }
}

fun Transformations.manipulation(block: Transform, options: Map<String, Any> = emptyMap()) {
    block(this, owner(), options)
}

fun Transformations.rule(block: Transform, options: Map<String, Any> = emptyMap()) {
    block(this, owner(), options)
}

fun Transformations.rule(blocks: List<Transform>, options: Map<String, Any> = emptyMap()) {
    blocks.forEach { it(this, owner(), options) }
}

private fun hasRefinement(name: String, source: String? = null): (KoreObject) -> Boolean = { obj ->
    obj.hasRefinement(name, source)
}

private fun canToFeature(name: String): (KoreObject) -> Boolean = { obj ->
    if (obj.hasRefinement(name)) {
        obj as KoreClass
        val properties = obj.attributes.filter { GeometryType.isInstance(it.type) }
        properties.size == 1 && properties.all { it.upperBound == 1 }
    } else {
        false
    }
}

private fun canToAttribute(name: String): (KoreObject) -> Boolean = { obj ->
    if (obj.hasRefinement(name)) {
        obj as KoreClass
        obj.attributes.none { GeometryType.isInstance(it.type) }
    } else {
        false
    }
}

private fun KoreObject.hasRefinement(name: String, source: String? = null): Boolean {
    return if (this is KoreModelElement) {
        getAnnotation(source)?.references?.filterIsInstance<KoreNamedElement>()?.any { it.name == name }
            ?: false
    } else false
}

private fun KoreObject.hasName(name: String): Boolean = this is KoreNamedElement && this.name == name

fun schemaName(name: String): (KoreObject) -> Boolean = { it ->
    if (it is KorePackage) {
        it.name == name && hasRefinement("applicationSchema")(it)
    } else {
        false
    }
}