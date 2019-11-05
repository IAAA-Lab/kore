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

import es.iaaa.kore.util.toPrettyString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RelationTableTest {
    @Test
    fun `creation of the relation and its human readable representation`() {
        val attribute1 = attributes("test_contents_1") {
            column { columnName = "id"; type = IntegerType; geoPackageSpec().add(PrimaryKey) }
        }
        val attribute2 = attributes("test_contents_2") {
            column { columnName = "id"; type = IntegerType; geoPackageSpec().add(PrimaryKey) }
        }
        val relation = relation("test_relations") {
            profile = "test contents"
            reference { columnName = "base_id"; type = attribute1; geoPackageSpec().add(BaseTable) }
            reference { columnName = "related_id"; type = attribute2; geoPackageSpec().add(RelatedTable) }
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
