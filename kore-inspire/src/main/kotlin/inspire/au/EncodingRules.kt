@file:Suppress("ObjectPropertyName")

package inspire.au

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transformations
import es.iaaa.kore.transform.rules.*

val `simple Feature Type stereotype to GeoPackage Feature`: Transformations.(Map<String, Any>) -> Unit = {
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature("featureType"))
}

val `Feature Type stereotype without geometry to GeoPackage Attribute`: Transformations.(Map<String, Any>) -> Unit = {
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute("featureType"))
}

val `Data Type stereotype to GeoPackage Attribute`: Transformations.(Map<String, Any>) -> Unit = {
    setMetMetaclassWhen(FeaturesTable, predicate = canToFeature("dataType"))
}

val `simple feature-like Data Type stereotype to GeoPackage Feature`: Transformations.(Map<String, Any>) -> Unit = {
    setMetMetaclassWhen(AttributesTable, predicate = canToAttribute("dataType"))
}

val `general rule ISO-19103 Basic Types`: Transformations.(Map<String, Any>) -> Unit = {
    setTypeWhen(TextType(), predicate = { it.type?.name == "CharacterString" })
    setTypeWhen(BooleanType(), predicate = { it.type?.name == "Boolean" })
    setTypeWhen(IntegerType(), predicate = { it.type?.name == "Integer" })
    setTypeWhen(DoubleType(), predicate = { it.type?.name == "Real" })
    setTypeWhen(TextType(), predicate = { it.type?.name == "Decimal" })
    setTypeWhen(DateTimeType(), predicate = { it.type?.name == "DateTime" })
    setTypeWhen(DateType(), predicate = { it.type?.name == "Date" })
}

val `general rule ISO-19107 Geometry Types`: Transformations.(Map<String, Any>) -> Unit = {
    setTypeWhen(CurveType(), predicate = { it.type?.name == "GM_Curve" })
    setTypeWhen(MultiSurfaceType(), predicate = { it.type?.name == "GM_MultiSurface" })
    setTypeWhen(PointType(), predicate = { it.type?.name == "GM_Point" })
}

val `general rule ISO-19139 Metadata XML Implementation Types`: Transformations.(Map<String, Any>) -> Unit = {
    setTypeWhen(TextType(), predicate = { it.type?.name == "URI" })
}

val `general rule Enumeration Types`: Transformations.(Map<String, Any>) -> Unit = { options ->
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

val `general rule CodeList Types`: Transformations.(Map<String, Any>) -> Unit = { options ->
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

val `voidable properties have a min cardinality of 0`: Transformations.(Map<String, Any>) -> Unit = {
    setLowerBoundWhen(0) { it.findDefaultNamedReferences().any { ref -> ref.name == "voidable" } }
}

val `flatten Data Types with upper cardinality of 1`: Transformations.(Map<String, Any>) -> Unit = {
    flattenTypes(predicate = hasRefinement("dataType"),
        postFlatten = { old, new ->
            new.name = "${old.name}_${new.name}"
            new.lowerBound = kotlin.math.min(old.lowerBound, new.lowerBound)
        })
}

fun Transformations.rule(block: Transformations.(Map<String, Any>) -> Unit, options: Map<String, Any> = emptyMap()) {
    block(this, options)
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

