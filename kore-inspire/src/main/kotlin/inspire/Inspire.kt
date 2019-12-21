package inspire

import es.iaaa.kore.transform.Conversion
import inspire.annex.i.au.au
import inspire.annex.i.gn.gn

fun main() {
    val works = mapOf(
        "au" to au,
        "gn" to gn)

    works.forEach { (name, conversion) -> creator(conversion, name) }
}

fun creator(conversion: (String, Map<String, Any>) -> Conversion, prefix: String) {
    val au = conversion(
        "kore-inspire/src/main/resources/INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml",
        mapOf(
            "description" to true,
            "name" to prefix,
            "sql" to true
        )
    )
    au.convert()
}