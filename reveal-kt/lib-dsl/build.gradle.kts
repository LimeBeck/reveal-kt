plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    alias(libs.plugins.publish)
}

val revealKtVersion: String by project

group = "dev.limebeck"
version = revealKtVersion

kotlin {
    metadata {
        mavenPublication {
            artifactId = "revealkt-dsl"
            pom {
                name.set("RevealKt kotlin-wrapper for Reveal JS library metadata")
                description.set("Kotlin metadata module for RevealKt kotlin-wrapper for Reveal JS library")
            }
        }
    }

    jvm {
        mavenPublication {
            artifactId = "revealkt-dsl-jvm"
            pom {
                name.set("RevealKt kotlin-wrapper for Reveal JS library JVM")
                description.set("Kotlin JVM module for RevealKt kotlin-wrapper for Reveal JS library")
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        mavenPublication {
            artifactId = "revealkt-dsl-js"
            pom {
                name.set("RevealKt kotlin-wrapper for Reveal JS library JS")
                description.set("Kotlin JS module for RevealKt kotlin-wrapper for Reveal JS library")
            }
        }
        binaries.executable()
        nodejs()
    }

//    Disabled native targets because only actual using is jvm/js for now and QrCode lib not published linux/windows targets :(
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native") {
//            mavenPublication {
//                artifactId = "revealkt-dsl-native-macos"
//                pom {
//                    name.set("RevealKt kotlin-wrapper for Reveal JS library native-macos")
//                    description.set("Kotlin native-macos module for RevealKt kotlin-wrapper for Reveal JS library")
//                }
//            }
//        }
//
//        hostOs == "Linux" -> linuxX64("native") {
//            mavenPublication {
//                artifactId = "revealkt-dsl-native-linux"
//                pom {
//                    name.set("RevealKt kotlin-wrapper for Reveal JS library native-linux")
//                    description.set("Kotlin native-linux module for RevealKt kotlin-wrapper for Reveal JS library")
//                }
//            }
//        }
//
//        isMingwX64 -> mingwX64("native") {
//            mavenPublication {
//                artifactId = "revealkt-dsl-native-win"
//                pom {
//                    name.set("RevealKt kotlin-wrapper for Reveal JS library native-win")
//                    description.set("Kotlin native-win module for RevealKt kotlin-wrapper for Reveal JS library")
//                }
//            }
//        }
//
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

    sourceSets {
        val commonMain by getting {
            //Hack for ksp working with Gradle 9
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")

            dependencies {
                implementation(libs.kxhtml)
                implementation(libs.uuid)
                api(libs.qrcode)
                api(libs.kotlin.css)
                implementation(libs.arrow.core)
                implementation(libs.arrow.optics)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines)
            }
        }

        val jvmMain by getting {
            dependencies {
            }
        }

        val jvmTest by getting {
            dependencies {
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

//        val nativeMain by getting
//        val nativeTest by getting {
//            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
//            }
//        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.arrow.ksp)
}

//Hack for ksp working with Gradle 9
listOf(
    "compileKotlinJs",
    "jsSourcesJar",
    "compileKotlinJvm",
    "jvmSourcesJar",
    "dokkaGeneratePublicationHtml",
    "sourcesJar"
).forEach {
    tasks.named(it).configure {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            val publicationName = this.name
            pom {
                if (publicationName == "kotlinMultiplatform") {
                    name.set("revealkt-dsl")
                    description.set("RevealJs kotlin wrapper and dsl")
                    artifactId = "revealkt-dsl"
                }
            }
        }
    }
}
