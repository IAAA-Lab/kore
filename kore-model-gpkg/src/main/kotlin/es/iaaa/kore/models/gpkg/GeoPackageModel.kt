/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.KoreReference
import es.iaaa.kore.KoreStructuralFeature

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
