import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `java-library`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx)
    alias(libs.plugins.shadow)
    alias(libs.plugins.koin)
}

version = rootProject.version

dependencies {
    compileOnly(project(":common"))

    compileOnly(libs.revxral.common)
    compileOnly(libs.revxral.bukkit)

    compileOnly(libs.folia)
}

tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    archiveVersion.set(project.version.toString())
    archiveBaseName.set(rootProject.name)
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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.withType<KotlinJvmCompile> {
    compilerOptions {
        javaParameters = true
    }
}