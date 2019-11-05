plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":kore-impl"))
    implementation(project(":kore-model-gpkg"))
    implementation(Libs.geopackage)

    testImplementation(Libs.unitJupiterApi)
    testRuntimeOnly(Libs.unitJupiterEngine)
}