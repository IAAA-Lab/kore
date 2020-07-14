/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
@file:Suppress("ObjectPropertyName")

package inspire.transformation

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.*
import es.iaaa.kore.transform.Transform
import es.iaaa.kore.transform.rules.patch

val `Association Roles`: Transform = { conversion, _ ->
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
                annotations.forEach { annotation -> annotation.copy(it) }
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
    addStereotype(Stereotypes.reference)
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
    relationName: String,
    relationProfile: String,
    base: KoreClass,
    related: KoreClassifier
): KoreClass = relation(relationName) {
    tableName = relationName
    profile = relationProfile
    foreignColumn {
        name = "base_id"
        columnName = "base_id"
        type = base
        isNavigable = true
        findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(BaseTable)
    }
    foreignColumn {
        name = "related_id"
        columnName = "related_id"
        type = related
        isNavigable = true
        findOrCreateAnnotation(GeoPackageSpec.SOURCE).references.add(RelatedTable)
    }
}
