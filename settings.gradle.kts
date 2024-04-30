rootProject.name = "reveal-kt"

rootDir.resolve("reveal-kt").list()?.forEach {
    include(":reveal-kt:$it")
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}
