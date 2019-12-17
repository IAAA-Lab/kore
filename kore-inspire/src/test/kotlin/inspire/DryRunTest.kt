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
package inspire

import es.iaaa.kore.transform.Conversion
import inspire.i.au.au
import inspire.i.au.mu
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class DryRunTest {

    @Test
    fun `Annex I Administrative Units - administrative units`() {
        checkSchema("i/au/au", au)
    }

    @Test
    fun `Annex I Administrative Units - maritime units - draft`() {
        checkSchema("i/au/mu", mu)
    }

    @Test
    fun `test geographical names`() {
        checkSchema("GeographicalNames", gn)
    }

    fun checkSchema(name: String, process: (String, Map<String, Any>) -> Conversion) {
        val run = process(
            "src/main/resources/$INSPIRE_CONSOLIDATED_UML_MODEL",
            mapOf("description" to false)
        )
        run.convert(true)
        val expected = File("src/test/resources/$name.txt").readText().split("\n")
        val dryRun = run.lastDryRunOutput.toString()
        val output = File("build/test/resources/$name.txt")
        output.parentFile.mkdirs()
        output.writeText(dryRun)
        val actual = dryRun.split("\n")
        expected.zip(actual).forEach {
            if (it.first.isBlank()) {
                assertTrue(it.second.isBlank())
            } else {
                assertEquals(it.first, it.second)
            }
        }
        assertEquals(expected.size, actual.size)
    }

    companion object {
        private const val INSPIRE_CONSOLIDATED_UML_MODEL =
            "INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml"
    }
}