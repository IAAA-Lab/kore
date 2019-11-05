plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    api(project(":kore-api"))

    implementation(kotlin("reflect"))

    testImplementation(Libs.unitJupiterApi)
    testRuntimeOnly(Libs.unitJupiterEngine)
}