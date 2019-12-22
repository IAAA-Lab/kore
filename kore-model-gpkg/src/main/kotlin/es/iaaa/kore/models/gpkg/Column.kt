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

import es.iaaa.kore.*

val Column = KoreModel.createClass().apply {
    name = "Column"
    attribute { name = "description" }
    attribute { name = "title" }
    attribute { name = "mimeType" }
    attribute { name = "columnName" }
    toString = prettyPrint()
}

fun column(init: KoreAttribute.() -> Unit): KoreAttribute = koreAttribute {
    metaClass = Column
    init()
}

fun KoreClass.column(init: KoreAttribute.() -> Unit): KoreAttribute = koreAttribute {
    metaClass = Column
    init()
    containingClass = this@column
}

fun KoreClass.attribute(init: KoreAttribute.() -> Unit) = koreAttribute {
    init()
    containingClass = this@attribute
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
