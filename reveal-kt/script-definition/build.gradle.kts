import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("java-library")
}

val revealKtVersion: String by project
val kotlinVersion: String by project

group = "dev.limebeck"
version = revealKtVersion

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven:$kotlinVersion")
    implementation(project(":reveal-kt:lib-dsl"))
}

sourceSets {
    test {}
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}