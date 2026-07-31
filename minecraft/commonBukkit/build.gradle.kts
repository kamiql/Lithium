plugins {
    alias(libs.plugins.kotlin)
    `java-library`
}

dependencies {
    api(project(":common"))
    compileOnly(libs.paper)
}