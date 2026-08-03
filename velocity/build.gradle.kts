import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx)
    alias(libs.plugins.shadow)
    alias(libs.plugins.koin)
    alias(libs.plugins.kapt)
}

version = rootProject.version

dependencies {
    implementation(project(":common"))

    implementation(libs.revxral.common)
    implementation(libs.revxral.velocity)

    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}

tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
    archiveVersion.set(project.version.toString())
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}