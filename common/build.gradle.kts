plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.koin)
    alias(libs.plugins.kotlinx)
}

version = rootProject.version

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))

    api(libs.koin.core)

    api(libs.kotlinx.json)
    api(libs.kotlinx.kaml)

    api(libs.luckperms)
    api(libs.adventure)

    api(libs.coroutines)
}

koinCompiler {
    userLogs = true
    debugLogs = true
}