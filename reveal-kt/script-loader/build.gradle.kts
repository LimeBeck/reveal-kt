import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("java-library")
}

val revealKtVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project

group = "dev.limebeck"
version = revealKtVersion

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutinesVersion}") {
        version {
            strictly(kotlinCoroutinesVersion)
        }
    }

    api("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")

    implementation(project(":reveal-kt:lib-dsl"))
    implementation(project(":reveal-kt:script-definition"))
}

sourceSets {
    test {}
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}