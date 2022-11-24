rootProject.name = "reveal-kt"

rootDir.resolve("reveal-kt").list()?.forEach {
    include(":reveal-kt:$it")
}

pluginManagement {
    val kotlinVersion: String by settings
    val kotestVersion: String by settings
    val dokkaVersion: String by settings
    val ktorVersion: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("io.kotest.multiplatform") version kotestVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("io.ktor.plugin") version ktorVersion
    }
}
