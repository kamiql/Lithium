plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.koin) apply false
    alias(libs.plugins.kotlinx) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.kapt) apply false
}

version = "0.1.0"
group = "net.lithium"
description = "Lithium server core system"

allprojects {
    repositories {
        mavenCentral()
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven("https://repo.kamiql.de/releases/")
    }
}

subprojects {
    plugins.withId("java") {
        tasks.withType<Jar>().configureEach {
            destinationDirectory.set(layout.buildDirectory.dir("libs/${project.version}"))
        }
    }
}