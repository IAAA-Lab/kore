plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":kore-impl"))
    implementation(project(":kore-resource"))
    implementation(project(":kore-io-gpkg"))
    implementation("com.andreapivetta.kolor:kolor:0.0.2") {
        exclude("org.jetbrains.kotlin")
    }

    testImplementation(Libs.unitJupiterApi)
    testRuntimeOnly(Libs.unitJupiterEngine)
}