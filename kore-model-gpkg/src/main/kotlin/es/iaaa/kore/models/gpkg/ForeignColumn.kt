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
import es.iaaa.kore.KoreModel
import es.iaaa.kore.KoreReference
import es.iaaa.kore.impl.KoreClassImpl

object ForeignColumn : KoreClassImpl() {
    init {
        name = "Foreign Column"
        superTypes.add(Column)
        toString = { if (it is KoreReference) it.prettyPrint() else "" }
    }

    operator fun invoke(init: KoreReference.() -> Unit): KoreReference = KoreModel.createReference().apply {
        metaClass = ForeignColumn
        init()
    }
}

fun KoreClass.reference(init: KoreReference.() -> Unit) {
    ForeignColumn.invoke(init).containingClass = this
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
