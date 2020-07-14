/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

object GeoPackageSpec {
    const val SOURCE = "http://www.geopackage.org/spec/"
}

fun KoreNamedElement.geoPackageSpec() = findOrCreateAnnotation(GeoPackageSpec.SOURCE).references

fun KoreNamedElement.findGeoPackageSpec() = getAnnotation(GeoPackageSpec.SOURCE)?.references

fun KoreStructuralFeature.isPrimaryKey(): Boolean {
    val refs = findGeoPackageSpec()
    return if (refs.isNullOrEmpty()) false else PrimaryKey in refs
}

fun KoreAttribute.findConstraint(): KoreClass? =
    findGeoPackageSpec()?.filterIsInstance<KoreClass>()?.find {
        Constraint.isInstance(it)
    }

fun KoreAttribute.findAllConstraints(): List<KoreClass> =
    findGeoPackageSpec()?.filterIsInstance<KoreClass>()?.filter {
        Constraint.isInstance(it)
    } ?: emptyList()

fun KoreReference.pointsToBaseTable(): Boolean {
    val refs = findGeoPackageSpec()
    return if (refs.isNullOrEmpty()) false else BaseTable in refs
}

fun KoreReference.pointsToRelatedTable(): Boolean {
    val refs = findGeoPackageSpec()
    return if (refs.isNullOrEmpty()) false else RelatedTable in refs
}

fun KoreClass?.isFeaturesTable(): Boolean = if (this != null) metaClass == FeaturesTable else false

fun KoreClass?.isAttributesTable(): Boolean = if (this != null) metaClass == AttributesTable else false

fun KoreClass?.isRelationTable(): Boolean = if (this != null) metaClass == RelationTable else false

fun KoreClass?.isEnumConstraint(): Boolean = if (this != null) metaClass == EnumConstraint else false

fun KoreClass?.hasTable(): Boolean = isFeaturesTable() || isAttributesTable() || isRelationTable()
