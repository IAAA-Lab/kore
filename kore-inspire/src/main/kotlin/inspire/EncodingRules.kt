@file:Suppress("ObjectPropertyName")

package inspire

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Conversion
import es.iaaa.kore.transform.Transformations
import es.iaaa.kore.transform.rules.*
import java.net.URL

typealias Transform = Transformations.(Conversion, Map<String, Any>) -> Unit

fun KoreClass?.isFeaturesTable(): Boolean = if (this != null) metaClass == FeaturesTable else false
fun KoreClass?.isAttributesTable(): Boolean = if (this != null) metaClass == AttributesTable else false
fun KoreClass?.isRelationTable(): Boolean = if (this != null) metaClass == RelationTable else false
fun KoreClass?.isEnumConstraint(): Boolean = if (this != null) metaClass == EnumConstraint else false
fun KoreClass?.hasTable(): Boolean = isFeaturesTable() || isAttributesTable() || isRelationTable()
fun KoreClass?.isPrefixable(): Boolean = hasTable() || isEnumConstraint()
fun KoreClass?.isMoveable(): Boolean = isFeaturesTable() || isAttributesTable() || isRelationTable() || isEnumConstraint()

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
    setTypeWhen(GeometryType(), predicate = { it.type?.name == "GM_Object" })
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
            .filter { it.isMoveable() }
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

val `add qualified name to prefixable elements`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { isPrefixable() }) {
        findTaggedValue("package_name")?.let {
            identifier = "$it::$name"
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

val `properties with maximum cardinality 1 to columns`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = {
        container?.metaClass in listOf(FeaturesTable, AttributesTable) &&
                !isMany
    }) {
        metaClass = Column
        columnName = name
    }
}

val `default package prefixes`: Transform = { _, _ ->
    patch<KoreClass>(predicate = { isPrefixable() }) {
        val term = if (isRelationTable()) tableName else name
        requireNotNull(term) {
            "Error found in a U2G class: we expect here a not null value in '${if (isRelationTable()) "tableName" else "name"}'"
        }
        val prefixedTerm = runCatching { lookupPrefix() }.getOrElse { assignPrefix() } + term
        if (hasTable()) {
            tableName = prefixedTerm
        }
        name = prefixedTerm
    }
}

fun KoreClass.assignPrefix(): String = when (container?.name) {
    "Cultural and linguistic adapdability" -> "GMD_"           // ISO 19139 freeText.xsd
    "ISO 00639 Language Codes" -> "GMD_"                       // ISO 19139 freeText.xsd
    "ISO 03166 Country Codes" -> "GMD_"                        // ISO 19139 freeText.xsd
    "Identification information" -> "GMD_"                     // ISO 19139 identification.xsd
    "Citation and responsible party information" -> "GMD_"     // ISO 19139 citation.xsd
    "Units of Measure" -> "UM_"
    "Data quality information" -> "GMD_"                       // ISO 19139 dataQuality.xsd
    "Enumerations" -> "GCO_"
    "Geometric primitive" -> ""
    null -> throw Exception("Not computable U2G prefix: No xmlns tag found in the hierarchy for package without name")
    else -> throw Exception("Not computable U2G prefix: No xmlns tag found in the hierarchy for package '${container?.name}'")
}

val `general rule for association roles and arrays`: Transform = { conversion, _ ->
    patch<KoreReference>(
        predicate = {
            name != null &&
                    containingClass?.metaClass in listOf(AttributesTable, FeaturesTable) &&
                    type?.metaClass in listOf(AttributesTable, FeaturesTable)
        }
    ) {
        val managedByOther = !required && (opposite?.required == true)
        if (!managedByOther) {
            val manyToOne = isMany && opposite?.isMany != true
            val oneToOne = !isMany

            val tableName =
                if (opposite?.required == true) "${opposite?.name}_$name" else "${containingClass?.name}_$name"
            val relationProfile = if (type?.metaClass == AttributesTable) "attributes" else "features"

            toRelation(tableName, relationProfile)?.let {
                it.relatedReference = when {
                    manyToOne -> opposite?.copyAsRefAttribute()
                    oneToOne -> copyAsRefAttribute()
                    else -> null
                }
                conversion.track(it)
            }

            containingClass = null
            opposite?.containingClass = null
        }
    }
}

private fun KoreReference?.copyAsRefAttribute(): KoreAttribute? = this?.toAttribute(remove = false)?.apply {
    type = IntegerType()
    lowerBound = 0
    upperBound = 1
}

private fun KoreReference.toRelation(
    tableName: String,
    relationProfile: String
): KoreClass? = type?.let { t ->
    containingClass?.let {
        it.container?.toRelation(tableName, relationProfile, it, t)
    }
}

private fun KorePackage.toRelation(
    tableName: String,
    relationProfile: String,
    base: KoreClass,
    related: KoreClassifier
): KoreClass = relation(tableName) {
    profile = relationProfile
    reference {
        name = "base_id"
        columnName = "base_id"
        type = base
        findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(BaseTable)
    }
    reference {
        name = "related_id"
        columnName = "related_id"
        type = related
        findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(RelatedTable)
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

private fun hasRefinement(name: String, source: String? = null): (KoreObject) -> Boolean = {
    it.hasRefinement(name, source)
}

private fun canToFeature(name: String): (KoreObject) -> Boolean = {
    when {
        it.hasRefinement(name) -> (it as? KoreClass)
            ?.attributes
            ?.filter { att -> GeometryType.isInstance(att.type) }
            ?.run { size == 1 && all { att -> att.upperBound == 1 } }
            ?: throw Exception("Not expected: if the instance has the refinement $name it must be a class")
        else -> false
    }
}

private fun canToAttribute(name: String): (KoreObject) -> Boolean = {
    when {
        it.hasRefinement(name) -> (it as? KoreClass)
            ?.attributes
            ?.none { att -> GeometryType.isInstance(att.type) }
            ?: throw Exception("Not expected: if the instance has the refinement $name it must be a class")
        else -> false
    }
}

private fun KoreObject.hasRefinement(name: String, source: String? = null): Boolean = when (this) {
    is KoreModelElement -> getAnnotation(source)
        ?.references
        ?.filterIsInstance<KoreNamedElement>()
        ?.any { it.name == name }
        ?: false
    else -> false
}

private fun KoreObject.hasName(name: String): Boolean = this is KoreNamedElement && this.name == name

fun schemaName(name: String): (KoreObject) -> Boolean = {
    when (it) {
        is KorePackage -> it.name == name && hasRefinement("applicationSchema")(it)
        else -> false
    }
}

/**
 * The prefix is the first occurrence of the tagged value xmlns in the hierarchy.
 */
fun KoreObject?.lookupPrefix(): String = when (this) {
    is KorePackage -> findTaggedValue("xmlns")
        ?.takeWhile { it != '#' }
        ?.toUpperCase()
        ?.replace("-", "_")
        ?.plus("_")
        ?: container.lookupPrefix()
    is KoreObject -> container.lookupPrefix()
    else -> throw Exception("Not computable U2G prefix: No xmlns tag found in the hierarchy")
}
