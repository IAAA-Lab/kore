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
package es.iaaa.kore.io.gpkg

import es.iaaa.kore.*
import es.iaaa.kore.models.gpkg.*
import mil.nga.geopackage.GeoPackage
import mil.nga.geopackage.attributes.AttributesColumn
import mil.nga.geopackage.attributes.AttributesTable
import mil.nga.geopackage.core.contents.Contents
import mil.nga.geopackage.core.contents.ContentsDataType
import mil.nga.geopackage.extension.GeometryExtensions
import mil.nga.geopackage.extension.MetadataExtension
import mil.nga.geopackage.extension.SchemaExtension
import mil.nga.geopackage.extension.related.ExtendedRelation
import mil.nga.geopackage.extension.related.RelatedTablesExtension
import mil.nga.geopackage.extension.related.RelationType
import mil.nga.geopackage.extension.related.UserMappingTable
import mil.nga.geopackage.features.columns.GeometryColumns
import mil.nga.geopackage.features.user.FeatureColumn
import mil.nga.geopackage.features.user.FeatureTable
import mil.nga.geopackage.manager.GeoPackageManager.create
import mil.nga.geopackage.manager.GeoPackageManager.open
import mil.nga.geopackage.metadata.MetadataScopeType
import mil.nga.geopackage.schema.columns.DataColumns
import mil.nga.geopackage.schema.constraints.DataColumnConstraintType
import mil.nga.geopackage.schema.constraints.DataColumnConstraints
import mil.nga.geopackage.user.custom.UserCustomColumn
import java.nio.file.Path
import java.nio.file.Paths
import mil.nga.geopackage.metadata.Metadata as GpkgMetadata

/**
 * Manager used to create and open GeoPackages.
 */
object ContainerManager {
    fun create(pkg: KorePackage, base: String, overwrite: Boolean): Boolean {
        val path = location(pkg, base) ?: return false
        val file = path.toFile()
        val effectiveFile = file.resolveSibling(file.name+".gpkg")
        if (effectiveFile.exists() && overwrite) {
            effectiveFile.delete()
        }
        return if (create(file)) {
            open(path.toFile()).use { geoPackage ->
                pkg.constraints { constraint -> geoPackage.createConstraint(constraint) }
                pkg.features { feature -> geoPackage.createFeature(feature) }
                pkg.attributes { attribute -> geoPackage.createAttribute(attribute) }
                pkg.relations { relations -> geoPackage.createRelation(relations) }
                pkg.metadata { metadata -> geoPackage.createMetadata(metadata) }
            }
            true
        } else false
    }

    fun openAndAdd(pkg: KorePackage, base: String) {
        val path = location(pkg, base)
        open(path?.toFile()).use { geoPackage ->
            pkg.constraints { constraint -> geoPackage.createConstraint(constraint) }
            pkg.features { feature -> geoPackage.createFeature(feature) }
            pkg.attributes { attribute -> geoPackage.createAttribute(attribute) }
            pkg.relations { attribute -> geoPackage.createRelation(attribute) }
        }
    }

    private fun GeoPackage.createFeature(feature: KoreClass) {
        val gt = GeometryType
        val geoColumn = feature.attributes.find { gt.isInstance(it.type) } ?: return
        val geoColumnName = geoColumn.columnName ?: return
        val geoColumnType = (geoColumn.type as? KoreDataType)?.ngaGeometryType ?: return
        val columns = feature.attributes.mapIndexed(createFeatureColumn())
        val contents = Contents().apply {
            tableName = feature.tableName
            dataType = ContentsDataType.FEATURES
            identifier = feature.identifier
            description = feature.description
            minX = feature.minX
            minY = feature.minY
            maxX = feature.maxX
            maxY = feature.maxY
            srs = (feature.srsId ?: 0).let { spatialReferenceSystemDao.queryForId(it) }
        }
        val geometryColumns = createGeometryColumn(contents, geoColumnName, geoColumnType)
        createGeometryColumnsTable()
        createFeatureTable(FeatureTable(feature.tableName, geoColumnName, columns))
        createDataColumns(contents, feature)
        geometryColumnsDao.create(geometryColumns)
        if (GeometryExtensions.isExtension(geoColumnType)) {
            GeometryExtensions(this).getOrCreate(feature.tableName, geoColumnName, geoColumnType)
        }
    }


    private fun createFeatureColumn(): (index: Int, KoreAttribute) -> FeatureColumn {
        return { index, column ->
            val columnName = column.columnName as String
            val type = column.type as KoreDataType
            val nonNull = column.lowerBound == 1
            val defaultValue = column.defaultValueLiteral
            val primaryKey = column.isPrimaryKey()
            val geometry = GeometryType.isInstance(type)
            val contrainedBlob = BlobType.isInstance(type) && type.isSet("maxSize")
            val contrainedText = TextType.isInstance(type) && type.isSet("maxCharCount")
            when {
                primaryKey -> FeatureColumn.createPrimaryKeyColumn(index, columnName)
                geometry -> FeatureColumn.createGeometryColumn(
                    index,
                    columnName,
                    type.ngaGeometryType,
                    nonNull,
                    defaultValue
                )
                contrainedBlob -> FeatureColumn.createColumn(
                    index,
                    columnName,
                    type.ngaGeoPackageDataType,
                    type.maxSize,
                    nonNull,
                    defaultValue
                )
                contrainedText -> FeatureColumn.createColumn(
                    index,
                    columnName,
                    type.ngaGeoPackageDataType,
                    type.maxCharCount,
                    nonNull,
                    defaultValue
                )
                else -> FeatureColumn.createColumn(index, columnName, type.ngaGeoPackageDataType, nonNull, defaultValue)
            }
        }
    }

    private fun createGeometryColumn(
        contents: Contents,
        geoColumnName: String,
        geoColumnType: mil.nga.sf.GeometryType
    ): GeometryColumns = GeometryColumns().apply {
        this.contents = contents
        columnName = geoColumnName
        geometryType = geoColumnType
        srs = contents.srs
        z = 2
        m = 2
    }


    private fun GeoPackage.createDataColumns(
        contents: Contents,
        table: KoreClass
    ) {
        createDataColumnsTable()
        contentsDao.create(contents)
        val dataColumns = table.attributes.map { column ->
            DataColumns().apply {
                this.contents = contents
                this.columnName = column.columnName
                this.name = column.name
                this.title = column.title
                this.description = column.description
                this.mimeType = column.mimeType
                this.constraintName = column.findConstraint()?.name?.toLowerCase()
            }
        }
        dataColumnsDao.create(dataColumns)
    }

    private fun GeoPackage.createAttribute(attributes: KoreClass) {
        with(attributes) {
            val columns = createAttributesColumns()
            val contents = Contents().apply {
                tableName = attributes.tableName
                dataType = ContentsDataType.ATTRIBUTES
                identifier = attributes.identifier
                description = attributes.description
            }
            createGeometryColumnsTable()
            createAttributesTable(AttributesTable(tableName, columns))
            createDataColumns(contents, attributes)
        }
    }

    private fun KoreClass.createAttributesColumns(withIndex: Boolean = true): List<AttributesColumn> =
        attributes.mapIndexed { index, column ->
            val columnName = column.columnName
            val type = column.type as KoreDataType
            val nonNull = column.lowerBound == 1
            val defaultValue = column.defaultValueLiteral
            val primaryKey = column.isPrimaryKey() && withIndex
            val contrainedBlob = BlobType.isInstance(type) && type.isSet("maxSize")
            val contrainedText = TextType.isInstance(type) && type.isSet("maxCharCount")
            when {
                primaryKey -> AttributesColumn.createPrimaryKeyColumn(index, columnName)
                contrainedBlob -> AttributesColumn.createColumn(
                    index,
                    columnName,
                    type.ngaGeoPackageDataType,
                    type.maxSize,
                    nonNull,
                    defaultValue
                )
                contrainedText -> AttributesColumn.createColumn(
                    index,
                    columnName,
                    type.ngaGeoPackageDataType,
                    type.maxCharCount,
                    nonNull,
                    defaultValue
                )
                else -> {
                    AttributesColumn.createColumn(
                        index,
                        columnName,
                        type.ngaGeoPackageDataType,
                        nonNull,
                        defaultValue
                    )
                }
            }
        }

    private fun KoreClass.createUserCustomColumns(withIndex: Boolean = true): List<UserCustomColumn> =
        attributes.mapIndexed { index, column ->
            val columnName = column.columnName
            val type = column.type as KoreDataType
            val nonNull = column.lowerBound == 1
            val defaultValue = column.defaultValueLiteral
            val primaryKey = column.isPrimaryKey() && withIndex
            val contrainedBlob = BlobType.isInstance(type) && type.isSet("maxSize")
            val contrainedText = TextType.isInstance(type) && type.isSet("maxCharCount")
            when {
                primaryKey -> UserCustomColumn.createPrimaryKeyColumn(index, columnName)
                contrainedBlob -> UserCustomColumn.createColumn(
                    index,
                    columnName,
                    type.ngaGeoPackageDataType,
                    type.maxSize,
                    nonNull,
                    defaultValue
                )
                contrainedText -> UserCustomColumn.createColumn(
                    index,
                    columnName,
                    type.ngaGeoPackageDataType,
                    type.maxCharCount,
                    nonNull,
                    defaultValue
                )
                else -> UserCustomColumn.createColumn(
                    index,
                    columnName,
                    type.ngaGeoPackageDataType,
                    nonNull,
                    defaultValue
                )
            }
        }

    private fun GeoPackage.createConstraint(constraint: KoreClass) {
        when (constraint.metaClass) {
            RangeConstraint -> createRangeConstraint(constraint)
            GlobConstraint -> createGlobConstraint(constraint)
            EnumConstraint -> createEnumConstraint(constraint)
        }
    }

    private fun GeoPackage.createRangeConstraint(rangeConstraint: KoreClass) {
        val constraint = DataColumnConstraints().apply {
            constraintName = rangeConstraint.name?.toLowerCase()
            constraintType = DataColumnConstraintType.RANGE
            description = rangeConstraint.description
            min = rangeConstraint.minRange
            max = rangeConstraint.maxRange
            minIsInclusive = rangeConstraint.minIsInclusive
            maxIsInclusive = rangeConstraint.maxIsInclusive
        }
        SchemaExtension(this).orCreate
        createDataColumnConstraintsTable()
        dataColumnConstraintsDao.create(constraint)
    }

    private fun GeoPackage.createGlobConstraint(globConstraint: KoreClass) {
        val constraint = DataColumnConstraints().apply {
            constraintName = globConstraint.name?.toLowerCase()
            constraintType = DataColumnConstraintType.GLOB
            description = globConstraint.description
            value = globConstraint.globValue
        }
        SchemaExtension(this).orCreate
        createDataColumnConstraintsTable()
        dataColumnConstraintsDao.create(constraint)
    }

    private fun GeoPackage.createEnumConstraint(enumConstraint: KoreClass) {
        val constraints = enumConstraint.attributes.map { attribute ->
            DataColumnConstraints().apply {
                constraintName = enumConstraint.name?.toLowerCase()
                constraintType = DataColumnConstraintType.ENUM
                description = attribute.description
                value = attribute.name
            }
        }
        SchemaExtension(this).orCreate
        createDataColumnsTable()
        createDataColumnConstraintsTable()
        dataColumnConstraintsDao.create(constraints)
    }


    private fun GeoPackage.createRelation(relation: KoreClass) {
        val mappingTable = UserMappingTable.create(
            relation.tableName,
            relation.createUserCustomColumns(withIndex = false)
        )
        val base = relation.allReferences().find { it.pointsToBaseTable() }?.type as? KoreClass
        val related = relation.allReferences().find { it.pointsToRelatedTable() }?.type as? KoreClass
        val relatedReference = relation.relatedReference
        val extendedRelation = ExtendedRelation().apply {
            baseTableName = base?.tableName
            basePrimaryColumn = base?.attributes?.find { it.isPrimaryKey() }?.columnName
            relatedTableName = related?.tableName
            relatedPrimaryColumn = related?.attributes?.find { it.isPrimaryKey() }?.columnName
            relationName =
                (if (FeaturesTable.isInstance(related)) RelationType.FEATURES else RelationType.ATTRIBUTES).name
            mappingTableName = relation.tableName
        }
        if (relatedReference != null) {
            execSQL(
            """
                CREATE VIEW '${relation.tableName}' (base_id, related_id) AS 
                SELECT id, ${relatedReference.columnName} FROM ${relatedReference.containingClass?.tableName}
                WHERE ${relatedReference.columnName} IS NOT NULL 
            """.trimIndent())
        } else {
            with(relation) {
                metaClass = AttributesTable
                column {
                    name = "id"; columnName = "id"; title = "Id"; description = "Id"; lowerBound = 1; type =
                    IntegerType(); geoPackageSpec().add(PrimaryKey)
                }
                references.forEach { it.toAttribute().apply {
                    metaClass = Column
                    columnName = name
                    type = IntegerType()
                    lowerBound = 1
                    upperBound = 1
                } }
            }
            createAttribute(relation)
        }
        with(RelatedTablesExtension(this)) {
            kotlin.runCatching {
                createUserMappingTable(mappingTable)
            }
        }
        createExtendedRelationsTable()
        extendedRelationsDao.create(extendedRelation)

    }

    private fun GeoPackage.createMetadata(md: KoreClass) {
        MetadataExtension(this).orCreate
        createMetadataTable()
        createMetadataReferenceTable()
        val gpkgmd = GpkgMetadata().apply {
            id = md.id?.toLong() ?: 0
            metadata = md.metadata
            metadataScope = md.scope?.let { MetadataScopeType.fromName(it) }
            mimeType = md.mimeType
            standardUri = md.standardUri
        }
        metadataDao.create(gpkgmd)
    }

    private fun KorePackage.features(process: (KoreClass) -> Unit) = process(FeaturesTable, process)

    private fun KorePackage.attributes(process: (KoreClass) -> Unit) = process(AttributesTable, process)

    private fun KorePackage.constraints(process: (KoreClass) -> Unit) = process(Constraint, process)

    private fun KorePackage.relations(process: (KoreClass) -> Unit) = process(RelationTable, process)

    private fun KorePackage.metadata(process: (KoreClass) -> Unit) = process(Metadata, process)

    private fun KorePackage.process(obj: KoreClass, process: (KoreClass) -> Unit) = classifiers
        .filter { obj.isInstance(it.metaClass) }
        .filterIsInstance<KoreClass>()
        .forEach(process)

    fun open(pkg: KorePackage, base: String): GeoPackage? {
        val path = location(pkg, base) ?: return null
        return open(path.toFile())
    }

    private fun location(pkg: KorePackage, base: String): Path? {
        val path = pkg.fileName ?: return null
        return Paths.get(base, path).toAbsolutePath()
    }
}
