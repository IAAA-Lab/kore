@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreClass
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.references
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch
import es.iaaa.kore.transform.rules.setTypeWhen

/**
 * The general rule for the stereotype `codeList` is the same as the [Enumerations] rule.
 */
val `Code Lists`: Transform = { _, options ->
    val withDescription = options["description"] == true
    val patched = mutableListOf<KoreClass>()
    patch<KoreClass>(predicate = { references(Stereotypes.codeList) }) {
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