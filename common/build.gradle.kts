plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.koin)
    alias(libs.plugins.kotlinx)
}

version = rootProject.version

dependencies {
    api(kotlin("stdlib-jdk8"))

    api(libs.koin.core)

    api(libs.kotlinx.json)
}

koinCompiler {
    userLogs = true
    debugLogs = true
}