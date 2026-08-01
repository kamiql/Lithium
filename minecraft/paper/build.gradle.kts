import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx)
    alias(libs.plugins.shadow)
    alias(libs.plugins.koin)
}

version = rootProject.version

dependencies {
    implementation(project(":common"))

    compileOnly(libs.paper)
}

tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    archiveVersion.set(project.version.toString())
    archiveBaseName.set(project.name)
    archiveClassifier.set("")
}

tasks.processResources {
    val props = mapOf(
        "version" to rootProject.version,
        "name" to rootProject.name,
        "description" to rootProject.description,
    )

    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {

        expand(props)
    }
}