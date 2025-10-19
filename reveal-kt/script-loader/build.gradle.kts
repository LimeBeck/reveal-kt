plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

val revealKtVersion: String by project

group = "dev.limebeck"
version = revealKtVersion

dependencies {
    implementation(libs.kotlin.coroutines)

    api(libs.kotlin.scripting.common)
    api(libs.kotlin.scripting.jvm)
    api(libs.kotlin.scripting.jvm.host)

    api(libs.qrcode)

    implementation(project(":reveal-kt:lib-dsl"))
    implementation(project(":reveal-kt:script-definition"))
}

publishing {
    publications {
        withType<MavenPublication> {
            artifactId = "revealkt-script-loader"
            pom {
                name.set("RevealKt kotlin-wrapper script loader for Reveal JS library")
                description.set("Kotlin script loader module for RevealKt kotlin-wrapper for Reveal JS library")
            }
        }
    }
}
