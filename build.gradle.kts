plugins {
    kotlin("multiplatform")
    id("com.github.ben-manes.versions") version "0.47.0"
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