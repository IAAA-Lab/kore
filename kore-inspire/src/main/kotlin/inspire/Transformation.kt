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
            alias.set(
                mapOf(
                    "eaxmiid25" to "2CF3B75E_8B1F_4853_B0B4_10C65AD71551", // Distance
                    "eaxmiid189" to "AE1AC547_B120_4488_A63F_60A8A7441D7A", // LocalisedCharacterString
                    "eaxmiid190" to "CB20C133_5AA4_4671_80C7_8ED2879AB0D9", // Identifier
                    "eaxmiid191" to "E548F6CD_653D_4dc7_AAF3_2E510C1453E0", // GeographicalName
                    "eaxmiid197" to "F8A23D50_CB8F_4c91_BD7F_C2082467D81A"  // PT_FreeText
                )
            )
        }
        transformation {
            manipulation(`Before rules`)
            rule(`ISO 19103 - Basic types`)
            rule(`ISO 19107 - Geometry types`)
            rule(`ISO 19139 - Metadata XML Implementation Types`)
            rule(`Flatten union types`)
            rule(`UoM is added as a separate property`)
            rule(`Flattening types`)
            rule(`Feature types`)
            rule(`Data types`)
            rule(Enumerations, options)
            rule(`Code Lists`, options)
            rule(Arrays)
            rule(`Association Roles`)
            rule(Voidable)
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