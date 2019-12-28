package inspire

import es.iaaa.kore.KoreModelElement
import es.iaaa.kore.KoreNamedElement
import es.iaaa.kore.KoreObject
import es.iaaa.kore.findTaggedValue
import es.iaaa.kore.resource.ResourceFactory
import es.iaaa.kore.resource.ResourceHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class InspireResourceHelperTest {

    @Test
    fun `eaxmiid entities are resolved`() {
        val file = "src/main/resources/$INSPIRE_CONSOLIDATED_UML_MODEL"
        val resource = ResourceFactory.createResource(File(file), "EA-UML1.3", inspireResourceHelper)
        assertTrue(resource.isLoaded)
        assertEquals(3, resource.warnings.filter { it.startsWith("### ") }.size)
    }

    companion object {
        private const val INSPIRE_CONSOLIDATED_UML_MODEL =
            "INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml"
    }
}

