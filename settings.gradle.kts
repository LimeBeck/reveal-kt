import org.gradle.kotlin.dsl.maven

rootProject.name = "reveal-kt"

rootDir.resolve("reveal-kt").listFiles()
    ?.filter { it.resolve("build.gradle.kts").isFile }
    ?.forEach { include(":reveal-kt:${it.name}") }

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
    }
}
