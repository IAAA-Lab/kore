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
@file:Suppress("ObjectPropertyName")

package inspire

import es.iaaa.kore.KoreReference
import es.iaaa.kore.references
import es.iaaa.kore.transform.conversion
import es.iaaa.kore.transform.impl.Console
import es.iaaa.kore.transform.impl.GeoPackageWriter
import es.iaaa.kore.transform.manipulation
import es.iaaa.kore.transform.rule
import inspire.transformation.*

val transformation = { options: Map<String, Any> ->
    conversion {
        input {
            type.set("EA-UML1.3")
            helper.set(inspireResourceHelper)
            boundary.set { source, target ->
                if (source is KoreReference) {
                    source.isNavigable && !target.references("featureType")
                } else {
                    !target.references("featureType")
                }
            }
        }
        transformation {
            manipulation(`Before rules`)
            rule(`Simple Citation`)
            rule(`Simple Geographical Name`)
            rule(`ISO 19103 - Basic types`)
            rule(`ISO 19107 - Geometry types`)
            rule(`ISO 19115 - Basic types`)
            rule(`ISO 19139 - Metadata XML Implementation Types`)
            rule(`Other types`)
            rule(`Replace boundaries by Identifier`)
            rule(`Flatten union types`)
            rule(`UoM is added as a separate property`)
            rule(`Flattening types`)
            rule(`Feature types`)
            rule(`Data types`)
            rule(`Enumerations and codelists`, options)
            rule(`Extract primitive array`)
            manipulation(`Convert potential references`)
            rule(`Association Roles`)
            rule(Voidable, options)
            rule(Properties)
            rule(`Naming modification`)
            manipulation(`After rules`)
        }
        output {
            add(Console)
            add(
                GeoPackageWriter(
                    overwrite = true,
                    base = "kore-inspire/templates"
                )
            )
        }
    }
}