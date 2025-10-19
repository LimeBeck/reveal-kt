plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

val revealKtVersion: String by project
val kotlinVersion: String = libs.versions.kotlin.get()

group = "dev.limebeck"
version = revealKtVersion

dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.serialization.plugin)

    api(libs.kotlin.scripting.common)
    api(libs.kotlin.scripting.jvm)
    api(libs.kotlin.scripting.jvm.host)
    api(libs.kotlin.scripting.dependencies)
    api(libs.kotlin.scripting.dependencies.maven.all)


    implementation(project(":reveal-kt:lib-dsl"))
}

publishing {
    publications {
        withType<MavenPublication> {
            artifactId = "revealkt-script-definition"
            pom {
                name = "RevealKt kotlin-wrapper script definition for Reveal JS library"
                description = "Kotlin script definition module for RevealKt kotlin-wrapper for Reveal JS library"
            }
        }
    }
}
