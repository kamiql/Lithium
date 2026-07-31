plugins {
    alias(libs.plugins.kotlin) apply false
}

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