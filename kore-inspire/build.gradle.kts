plugins {
    application
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":kore-impl"))
    implementation(project(":kore-transform"))
    implementation(project(":kore-model-gpkg"))
    implementation(project(":kore-resource"))
    implementation("khttp:khttp:1.0.0") {
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.spek")
    }

    testImplementation(Libs.unitJupiterApi)
    testRuntimeOnly(Libs.unitJupiterEngine)
}
