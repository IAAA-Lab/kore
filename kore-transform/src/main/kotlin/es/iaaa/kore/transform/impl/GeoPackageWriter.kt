/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.transform.impl

import es.iaaa.kore.impl.Violations
import es.iaaa.kore.io.gpkg.ContainerManager
import es.iaaa.kore.transform.Model
import es.iaaa.kore.transform.OutputWriter
import java.nio.file.Paths

class GeoPackageWriter(
    var open: Boolean = false,
    var overwrite: Boolean = false,
    var base: String = "."
) : OutputWriter {
    override fun write(resource: Model, context: Map<String, Any>) {
        resource.selectedPackages().forEach {
            val name = context.getOrDefault("name", null) as? String?
            val sql = context.getOrDefault("sql", false) as? Boolean? ?: false
            if (open) {
                ContainerManager.openAndAdd(it, base, name)
            } else {
                ContainerManager.create(it, base, name, overwrite)
            }
            if (sql && name != null) {
                val workingDir = Paths.get(base).toAbsolutePath()
                val process = ProcessBuilder("sqlite3", "$name.gpkg", ".dump")
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .directory(workingDir.toFile())
                    .start()
                val target = process.inputStream.bufferedReader().readText()
                workingDir.resolve("$name.sql").toFile().printWriter().use { printer ->
                    printer.println("PRAGMA application_id=1196444487;")
                    printer.println("PRAGMA user_version=10201;")
                    printer.println(target)
                }
            }
        }
    }

    override fun validate(): Violations = Violations()
}
