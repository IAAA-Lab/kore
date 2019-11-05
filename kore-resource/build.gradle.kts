plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":kore-impl"))
    implementation("org.jdom:jdom2:2.0.6")
    implementation("jaxen:jaxen:1.2.0")
}