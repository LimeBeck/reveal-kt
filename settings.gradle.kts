
rootProject.name = "reveal-kt"

rootDir.resolve("reveal-kt").list()?.forEach {
    include(":reveal-kt:$it")
}

pluginManagement {
    val kotlinVersion: String by settings
    val kotestVersion: String by settings
    val dokkaVersion: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion
        id("io.kotest.multiplatform") version kotestVersion
        id("org.jetbrains.dokka") version dokkaVersion
    }
}
