import org.gradle.kotlin.dsl.maven

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

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
    }
}
