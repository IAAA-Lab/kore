import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Calendar.*

plugins {
    base
    kotlin("jvm") version "1.3.61" apply false
    id("com.github.hierynomus.license") version "0.15.0"
}

allprojects {
    group = "es.iaaa.kore"
    version = "1.0-SNAPSHOT"
    apply(plugin = "com.github.hierynomus.license")
    repositories {
        jcenter()
    }
    license {
        header = rootProject.file("gradle/check/license-header.txt")
        strictCheck = true
        license {
            ext.set("year", getInstance().get(YEAR))
        }
        excludes(listOf("**/*.xml", "**/*.txt"))
    }
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
