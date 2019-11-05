plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":kore-impl"))
    implementation(Libs.geopackage)

    testImplementation(project(":kore-transform"))
    testImplementation(Libs.unitJupiterApi)
    testRuntimeOnly(Libs.unitJupiterEngine)
}