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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class DryRunTest {

    @Test
    fun `Annex I Addresses - addresses - draft`() {
        checkSchema("Addresses", "annex/i/ad/ad")
    }

    @Test
    fun `Annex I Administrative Units - administrative units`() {
        checkSchema("AdministrativeUnits", "annex/i/au/au")
    }

    @Test
    fun `Annex I Administrative Units - maritime units - draft`() {
        checkSchema("MaritimeUnits", "annex/i/au/mu")
    }

    @Test
    fun `Annex I Geographical Names - geographical names - draft`() {
        checkSchema("Geographical Names", "annex/i/gn/gn")
    }

    fun checkSchema(schema: String, route: String) {
        val config = Configuration(
            file = "src/main/resources/$INSPIRE_CONSOLIDATED_UML_MODEL",
            description = false,
            sql = false,
            metadata = false
        )
        val run = configuration(schema, route, config)
        run.convert(true)
        val expected = File("src/test/resources/$route.txt").readText().split("\n")
        val dryRun = run.lastDryRunOutput.toString()
        val output = File("build/test/resources/$route.txt")
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