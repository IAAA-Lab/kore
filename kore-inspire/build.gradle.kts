plugins {
    application
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":kore-impl"))
    implementation(project(":kore-transform"))
    implementation(project(":kore-model-gpkg"))
    implementation("khttp:khttp:1.0.0") {
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.spek")
    }

    testImplementation(project(":kore-resource"))
    testImplementation(Libs.unitJupiterApi)
    testRuntimeOnly(Libs.unitJupiterEngine)
}
