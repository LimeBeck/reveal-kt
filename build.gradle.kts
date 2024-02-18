plugins {
    kotlin("multiplatform")
    alias(libs.plugins.versions)
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