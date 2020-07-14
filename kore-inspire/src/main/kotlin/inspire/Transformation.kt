/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
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
                    source.isNavigable && !target.references(Stereotypes.featureType)
                } else {
                    !target.references(Stereotypes.featureType)
                }
            }
        }
        transformation {
            manipulation(`Before rules`)
            rule(`Simple Citation`)
            rule(`Simple Geographical Name`)
            rule(`Simple Localised Character String`)
            rule(`ISO 19103 - Basic types`)
            rule(`ISO 19107 - Geometry types`)
            rule(`ISO 19115 - Basic types`)
            rule(`ISO 19139 - Metadata XML Implementation Types`)
            rule(`Other types`)
            rule(`Replace boundaries by Identifier`)
            rule(`Flatten union types`)
            rule(`UoM is added as a separate property`)
            rule(`Flattening types`)
            rule(`Move voidable geometries in feature types with multiple geometries to related feature types`)
            rule(`Feature types with single geometry to feature table`)
            rule(`Feature types with no geometry to attribute table`)
            rule(`Data types`)
            rule(`Enumerations and codelists`, options)
            rule(`Extract primitive array`)
            manipulation(`Convert potential references`)
            rule(`Replace abstract types in properties by identifier`)
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