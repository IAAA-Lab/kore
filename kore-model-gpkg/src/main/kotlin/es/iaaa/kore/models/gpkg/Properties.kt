/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.KorePackage
import es.iaaa.kore.KoreStructuralFeature
import es.iaaa.kore.impl.KoreStorage
import java.math.BigDecimal

/**
 * The table name of the actual content table.
 */
var KoreClass.tableName: String? by KoreStorage()

/**
 * The profile of the relation table.
 */
var KoreClass.profile: String? by KoreStorage()

/**
 * The column name of a column.
 */
var KoreStructuralFeature.columnName: String? by KoreStorage()

/**
 * The title of a column.
 */
var KoreAttribute.title: String? by KoreStorage()

/**
 * The MIME type of a column.
 */
var KoreAttribute.mimeType: String? by KoreStorage()

/**
 * The MIME type of a metadata.
 */
var KoreClass.mimeType: String? by KoreStorage()

/**
 * A human-readable identifier (e.g. short name) for the content.
 */
var KoreClass.identifier: String? by KoreStorage()

/**
 * A human-readable description for the content.
 */
var KoreClass.description: String? by KoreStorage()

/**
 * A human-readable description for the content.
 */
var KoreAttribute.description: String? by KoreStorage()

/**
 * Spatial reference system id.
 */
var KoreClass.srsId: Long? by KoreStorage()

/**
 * Bounding box minimum easting or longitude for all content.
 */
var KoreClass.minX: Double? by KoreStorage()

/**
 * Bounding box minimum easting or longitude for all content.
 */
var KoreClass.minY: Double? by KoreStorage()

/**
 * Bounding box minimum easting or longitude for all content.
 */
var KoreClass.maxX: Double? by KoreStorage()

/**
 * Bounding box minimum easting or longitude for all content.
 */
var KoreClass.maxY: Double? by KoreStorage()

/**
 * Minimum value for range.
 */
var KoreClass.minRange: BigDecimal? by KoreStorage()

/**
 * Maximum value for range.
 */
var KoreClass.maxRange: BigDecimal? by KoreStorage()

/**
 * Minimum is inclusive.
 */
var KoreClass.minIsInclusive: Boolean? by KoreStorage()

/**
 * Maximum is inclusive.
 */
var KoreClass.maxIsInclusive: Boolean? by KoreStorage()

/**
 * Glob expresion.
 */
var KoreClass.globValue: String? by KoreStorage()

/**
 * Case sensitive name of the data scope to which this metadata applies.
 */
var KoreClass.scope: String? by KoreStorage()

/**
 * URI reference to the metadata structure definition authority
 */
var KoreClass.standardUri: String? by KoreStorage()

/**
 * Metadata
 */
var KoreClass.metadata: String? by KoreStorage()

/**
 * Related attribute
 */
var KoreClass.relatedReference: KoreAttribute? by KoreStorage()

/**
 * The location of the container.
 */
var KorePackage.fileName: String? by KoreStorage()

