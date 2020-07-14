/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.models.gpkg

import es.iaaa.kore.util.toPrettyString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RelationTableTest {
    @Test
    fun `creation of the relation and its human readable representation`() {
        container {
            val attribute1 = attributes("test_contents_1") {
                tableName = "test_contents_1"
                idColumn()
            }
            val attribute2 = attributes("test_contents_2") {
                tableName = "test_contents_2"
                idColumn()
            }
            val relation = relation("test_relations") {
                profile = "test contents"
                tableName = "test_relations"
                foreignColumn { columnName = "base_id"; type = attribute1; geoPackageSpec().add(BaseTable) }
                foreignColumn { columnName = "related_id"; type = attribute2; geoPackageSpec().add(RelatedTable) }
            }
            assertEquals(RelationTable, relation.metaClass)
            assertEquals("test_relations", relation.tableName)
            assertEquals(
                """
           |Relation "test_relations" {
           |  profile = "test contents"
           |  tableName = "test_relations"
           |  base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES test_contents_1(id)
           |  related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES test_contents_2(id)
           |}
           """.trimMargin(),
                relation.toPrettyString()
            )
        }
    }
}
