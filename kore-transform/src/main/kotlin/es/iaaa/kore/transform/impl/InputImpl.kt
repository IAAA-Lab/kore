/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.impl

import es.iaaa.kore.KoreObject
import es.iaaa.kore.KorePackage
import es.iaaa.kore.resource.ResourceHelper
import es.iaaa.kore.transform.Input
import es.iaaa.kore.transform.Property

/**
 * Implementation of a builder of [Input].
 */
class InputImpl(
    override var file: Property<String> = Property("file"),
    override var type: Property<String> = Property("type"),
    override var selector: Property<(KorePackage) -> Boolean> = Property("selector") { _ -> true },
    override var helper: Property<ResourceHelper> = Property("helper"),
    override var boundary: Property<(KoreObject, KoreObject) -> Boolean> = Property("include") { _, _ -> true }
) : Input
