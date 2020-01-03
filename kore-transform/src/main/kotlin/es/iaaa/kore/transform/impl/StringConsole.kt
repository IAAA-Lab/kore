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
package es.iaaa.kore.transform.impl

import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.KorePackage
import es.iaaa.kore.impl.Violations
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.OutputWriter
import es.iaaa.kore.util.toPrettyString

class StringConsole(private val out: StringBuffer = StringBuffer()) : OutputWriter {
    override fun write(resource: Model, context: Map<String, Any>) {
        resource.allRelevantContent().mapNotNull {
            when (it) {
                is KorePackage -> it
                is KoreClassifier -> it.container
                else -> null
            }
        }.distinct().forEach {
            out.appendln(it.toPrettyString(resource.allRelevantContent()))
        }
    }

    override fun validate(): Violations = Violations()
}
