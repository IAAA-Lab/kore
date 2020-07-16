/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

object Column : KoreClass by koreClass({
    name = "Column"
    attribute { name = "description" }
    attribute { name = "title" }
    attribute { name = "mimeType" }
    attribute { name = "columnName" }
    toString = prettyPrint()
})

fun KoreClass.column(init: KoreAttribute.() -> Unit = {}): KoreAttribute = koreAttribute(Column) {
    containingClass = this@column
    init()
    verify(containingClass == this@column) { "The containing class property has muted within the block" }
}

fun KoreClass.idColumn(init: KoreAttribute.() -> Unit = {}): KoreAttribute = koreAttribute(Column) {
    containingClass = this@idColumn
    columnName = "id"
    lowerBound = 1
    upperBound = 1
    val intType = IntegerType()
    type = intType
    geoPackageSpec().add(PrimaryKey)
    name = "id"
    title = "Id"
    description = "Id"
    init()
    verify(containingClass == this@idColumn) { "The containing class property has muted within the block" }
    verify(columnName == "id") { "The column name property has muted within the block" }
    verify(lowerBound == 1) { "The lower bound property has muted within the block" }
    verify(upperBound == 1) { "The upper bound property has muted within the block" }
    verify(type == intType) { "The type property has muted within the block" }
    verify(geoPackageSpec().contains(PrimaryKey)) { "The primary key property has muted within the block" }
}

fun prettyPrint(): (KoreObject) -> String = { it ->
    with(it as KoreAttribute) {
        with(StringBuilder()) {
            append(columnName)
            append(" ")
            append(type?.name ?: "<<missing>>")
            when {
                lowerBound == 1 && upperBound == 1 -> append(" NOT NULL")
                lowerBound != 0 && upperBound != 1 -> append(" <<check [$lowerBound..$upperBound]>>")
            }
            if (isPrimaryKey()) {
                append(" PRIMARY KEY")
            }
            val constraints = findAllConstraints().map { it.name }
            if (constraints.isNotEmpty()) {
                constraints.joinTo(this, prefix = " CHECK(", separator = " ,", postfix = ")")
            }
            if (defaultValueLiteral != null) {
                append(" DEFAULT '$defaultValueLiteral'")
            }
            toString()
        }
    }
}
