/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.KoreClass
import es.iaaa.kore.koreClass

object PrimaryKey : KoreClass by koreClass({
    name = "PrimaryKey"
})

object BaseTable : KoreClass by koreClass({
    name = "BaseTable"
})

object RelatedTable : KoreClass by koreClass({
    name = "RelatedTable"
})
