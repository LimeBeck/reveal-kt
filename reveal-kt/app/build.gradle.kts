import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
    alias(libs.plugins.build.config)
}

val revealKtVersion: String by project
group = "dev.limebeck"
version = revealKtVersion

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

buildTimeConfig {
    config {
        destination.set(project.buildDir)
        objectName.set("RevealkConfig")
        packageName.set("dev.limebeck.revealkt")
        configProperties {
            property<String>("version") set revealKtVersion
        }
    }
}

kotlin {
    jvm {
        java {
//            withSourcesJar()
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        binaries.executable()
        useCommonJs()
        browser {
            commonWebpackConfig {
                outputFileName = "revealkt.js"
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":reveal-kt:lib-dsl"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${libs.versions.serialization.get()}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(project(":reveal-kt:script-definition"))
                implementation(project(":reveal-kt:script-loader"))
                implementation("io.ktor:ktor-server-cio:${libs.versions.ktor.get()}")
                implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
                implementation("io.ktor:ktor-server-html-builder-jvm:${libs.versions.ktor.get()}")
                implementation(libs.kxhtml.jvm)
                implementation(libs.logback)
                implementation(libs.slf4j)
                implementation(libs.clikt)
                implementation(libs.playwright)
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.346")
                implementation(npm("reveal.js", "5.0.2"))
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("dev.limebeck.application.ApplicationKt")
}

val jvmProcessResources = tasks.named<Copy>("jvmProcessResources")

val jsCopyTask = tasks.create<Copy>("jsCopyTask") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
    into(jvmProcessResources.get().destinationDir.resolve("js"))
    excludes.add("*.zip")
    excludes.add("*.tar")
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}

val stubJavaDocJar by tasks.registering(Jar::class) {
    archiveClassifier.value("javadoc")
}

// tasks to create an executable jar with all components of the app
val shadow = tasks.getByName<ShadowJar>("shadowJar") {
    dependsOn(jsCopyTask) // make sure JS gets compiled first
    archiveClassifier.set("")
    mergeServiceFiles()
    finalizedBy(stubJavaDocJar)
}

tasks.named("jvmJar") {
    dependsOn(jsCopyTask)
}

tasks.named("publish") {
    dependsOn(shadow)
}

publishing {
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
         create<MavenPublication>("shadow") {
            project.shadow.component(this)
            artifact(tasks["sourcesJar"])
            artifact(stubJavaDocJar)
            artifactId = "revealkt-cli"
            pom {
                name.set("RevealKt kotlin-wrapper CLI for Reveal JS library")
                description.set("Kotlin cli module for RevealKt kotlin-wrapper for Reveal JS library")
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

tasks.withType<PublishToMavenRepository>().configureEach {
    mustRunAfter(":reveal-kt:app:signKotlinMultiplatformPublication")
    onlyIf {
        it.name.contains("shadow", ignoreCase = true)
    }
}

tasks.withType<PublishToMavenLocal>().configureEach {
    mustRunAfter(":reveal-kt:app:signKotlinMultiplatformPublication")
    onlyIf {
        it.name.contains("shadow", ignoreCase = true)
    }
}

signing {
    sign(publishing.publications)
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
}