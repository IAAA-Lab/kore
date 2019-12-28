@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.KorePackage
import es.iaaa.kore.copy
import es.iaaa.kore.models.gpkg.TextType
import es.iaaa.kore.models.gpkg.enumConstraint
import es.iaaa.kore.models.gpkg.geoPackageSpec
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

/**
 * The unit of measurement attribute (`uom`) on any property `x` has to be retained.
 * It is transformed to a new property of the type `TEXT` with the name `x_uom`.
 *
 * The syntax and value space of the new property is the same of `gml:UomIdentifier`.
 * The legal values of the new property are the literals of the data column constraint enum `GML_UomIdentifier`.
 * This restriction may be enforced by SQL triggers or by code in applications that update GeoPackage data values.
 */
val `UoM is added as a separate property`: Transform = { conversion, _ ->

    val uomPackage by lazy {
        conversion.model.allContent().filterIsInstance<KorePackage>().find { it.name == "Units of Measure" }
            ?: throw Exception("Essential package 'Units of Measure' not found")
    }

    val uom by lazy {
        uomPackage.enumConstraint("UomIdentifier")
    }

    val measure by lazy {
        uomPackage.findClassifier("Measure") ?: throw Exception("Essential class 'Measure' not found")
    }

    patch<KoreAttribute>(predicate = {
        val candidate = type
        upperBound == 1 &&
                candidate is KoreClass &&
                (measure in candidate.allSuperTypes() || candidate == measure)
    }) {
        type = TextType()
        copy().apply {
            name = this@patch.name + "_uom"
            type = TextType()
            geoPackageSpec().add(uom)
        }
    }
}