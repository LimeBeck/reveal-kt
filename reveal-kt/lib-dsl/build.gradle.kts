plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
    id("io.kotest.multiplatform")
}

val revealKtVersion: String by project

val kotlinCoroutinesVersion: String by project
val kotestVersion: String by project
val kotlinxHtmlVersion: String by project

group = "dev.limebeck"
version = revealKtVersion

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

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
            artifactId = "revealkt-jvm"
            pom {
                name.set("RevealKt kotlin-wrapper for Reveal JS library JVM")
                description.set("Kotlin JVM module for RevealKt kotlin-wrapper for Reveal JS library")
            }
        }
        compilations.all {
//            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        mavenPublication {
            artifactId = "revealkt-js"
            pom {
                name.set("RevealKt kotlin-wrapper for Reveal JS library JS")
                description.set("Kotlin JS module for RevealKt kotlin-wrapper for Reveal JS library")
            }
        }
        binaries.executable()
        nodejs()
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native") {
            mavenPublication {
                artifactId = "revealkt-native-macos"
                pom {
                    name.set("RevealKt kotlin-wrapper for Reveal JS library native-macos")
                    description.set("Kotlin native-macos module for RevealKt kotlin-wrapper for Reveal JS library")
                }
            }
        }

        hostOs == "Linux" -> linuxX64("native") {
            mavenPublication {
                artifactId = "revealkt-native-linux"
                pom {
                    name.set("RevealKt kotlin-wrapper for Reveal JS library native-linux")
                    description.set("Kotlin native-linux module for RevealKt kotlin-wrapper for Reveal JS library")
                }
            }
        }

        isMingwX64 -> mingwX64("native") {
            mavenPublication {
                artifactId = "revealkt-native-win"
                pom {
                    name.set("RevealKt kotlin-wrapper for Reveal JS library native-win")
                    description.set("Kotlin native-win module for RevealKt kotlin-wrapper for Reveal JS library")
                }
            }
        }

        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinxHtmlVersion")
                implementation("com.benasher44:uuid:0.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation("io.kotest:kotest-framework-datatest:$kotestVersion")
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutinesVersion}") {
                    version {
                        strictly(kotlinCoroutinesVersion)
                    }
                }
            }
        }
        val jvmMain by getting {
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
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
        val nativeMain by getting
        val nativeTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
            }
        }
    }
}

val stubJavaDocJar by tasks.registering(Jar::class) {
    archiveClassifier.value("javadoc")
}

publishing {
    kotlin.targets.forEach { target ->
        val targetPublication: Publication? = publications.findByName(target.name)
        if (targetPublication is MavenPublication) {
            targetPublication.artifact(stubJavaDocJar.get())
        }
    }

    repositories {
        maven {
            name = "MainRepo"
            url = uri(
                System.getenv("REPO_URI")
                    ?: project.findProperty("repo.uri") as String
            )
            credentials {
                username = System.getenv("REPO_USERNAME")
                    ?: project.findProperty("repo.username") as String?
                password = System.getenv("REPO_PASSWORD")
                    ?: project.findProperty("repo.password") as String?
            }
        }
    }

    publications {
        withType<MavenPublication> {
            val publicationName = this.name
            pom {
                if (publicationName == "kotlinMultiplatform") {
                    name.set("revealkt")
                    description.set("RevealJs kotlin wrapper and dsl")
                }
                groupId = "dev.limebeck"
                url.set("https://github.com/LimeBeck/reveal-kt")
                developers {
                    developer {
                        id.set("LimeBeck")
                        name.set("Anatoly Nechay-Gumen")
                        email.set("mail@limebeck.dev")
                    }
                }
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://github.com/LimeBeck/reveal-kt/blob/master/LICENCE")
                        distribution.set("repo")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/LimeBeck/reveal-kt.git")
                    developerConnection.set("scm:git:ssh://github.com/LimeBeck/reveal-kt.git")
                    url.set("https://github.com/LimeBeck/reveal-kt")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications)
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}