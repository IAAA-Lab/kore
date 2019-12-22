package inspire.transformation

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.models.gpkg.AttributesTable
import es.iaaa.kore.models.gpkg.Column
import es.iaaa.kore.models.gpkg.FeaturesTable
import es.iaaa.kore.models.gpkg.columnName
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Properties`: Transform = { _, _ ->
    patch<KoreAttribute>(predicate = {
        container?.metaClass in listOf(
            FeaturesTable,
            AttributesTable
        ) &&
                !isMany
    }) {
        metaClass = Column
        columnName = name
    }
}