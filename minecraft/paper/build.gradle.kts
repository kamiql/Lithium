plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    api(project(":minecraft:commonBukkit"))

    compileOnly(libs.paper)
}