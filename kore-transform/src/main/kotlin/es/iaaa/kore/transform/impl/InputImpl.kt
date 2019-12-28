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
    override var helper: Property<ResourceHelper> = Property("helper")
) : Input
