@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.mapEntry

/**
 * All types that have the stereotype `enumeration`are converted to GeoPackage data column constraints of type enum.
 */

val enumerations: Transform = processLists(Stereotypes.enumeration)

/**
 * The general rule for the stereotype `codeList` is the same as the [enumerations] rule.
 */
val codeLists: Transform = processLists(Stereotypes.codeList)

val `Enumerations and codelists`: List<Transform> = listOf(
    enumerations,
    codeLists
)

private fun processLists(
    target: String
): Transform = { conversion, options ->
    val withDescription = options["description"] == true
    val patched = mutableListOf<KoreClass>()
    patch<KoreClass>(predicate = { references(target) }) {
        metaClass = EnumConstraint
        attributes.forEach {
            it.metaClass = EnumConstraintValue
            if (withDescription) {
                val uri = "http://inspire.ec.europa.eu/${target.toLowerCase()}/${it.containingClass?.name}/${it.name}"
                if (khttp.get(uri).statusCode == 200) {
                    it.description = uri
                }
            }
        }
        patched.add(this)
        conversion.track(this)
    }
    mapEntry(typePredicate = {
        it.type in patched
    }, preset = {
        val type = it.type
        if (type is KoreClass) {
            it.geoPackageSpec().add(type)
        }
        conversion.track(it)
    }, targetType = TextType())
}