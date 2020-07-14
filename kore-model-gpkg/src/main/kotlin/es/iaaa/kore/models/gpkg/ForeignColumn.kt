/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.*

object ForeignColumn : KoreClass by koreClass({
    name = "Foreign Column"
    superTypes.add(Column)
    toString = { if (it is KoreReference) it.prettyPrint() else "" }
})

fun KoreClass.foreignColumn(init: KoreReference.() -> Unit) = koreReference(ForeignColumn) {
    containingClass = this@foreignColumn
    init()
    verify(containingClass == this@foreignColumn) { "The containing class property has muted within the block" }
}

private fun KoreReference.prettyPrint(): String =
    with(StringBuilder()) {
        val otherTable = (type as? KoreClass)?.tableName ?: "<<missing table name>>"
        val primaryKey = (type as? KoreClass)?.structuralFeatures?.find { it.isPrimaryKey() } as? KoreAttribute?
        val primaryKeyTypeName = primaryKey?.type?.name ?: "<<missing type name>>"
        val primaryKeyName = primaryKey?.columnName ?: "<<missing name>>"
        append("$columnName $primaryKeyTypeName NOT NULL")
        append(" FOREIGN KEY ($columnName) REFERENCES $otherTable($primaryKeyName)")
        toString()
    }
