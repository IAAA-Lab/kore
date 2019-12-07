import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

/**
 * Configures the current project as a Kotlin project by adding the Kotlin `stdlib` as a dependency.
 */
fun Project.kotlinProject() {
    dependencies {
        "implementation"(kotlin("stdlib-jdk8"))
    }
}

object Versions {
    const val ktlintPlugin = "9.1.0"

    const val ktlint = "0.35.0"
}

object Libs {
    const val geopackage = "mil.nga.geopackage:geopackage:3.4.0"
    const val unitJupiterApi = "org.junit.jupiter:junit-jupiter-api:5.5.1"
    const val unitJupiterEngine = "org.junit.jupiter:junit-jupiter-engine:5.5.1"
}
