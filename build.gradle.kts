plugins {
    kotlin("multiplatform")
    id("com.github.ben-manes.versions") version "0.42.0"
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {}
    sourceSets {
        val commonMain by getting {
            dependencies {

            }
        }
//        val jvmMain by getting
    }
}