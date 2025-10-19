import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.build.config)
    alias(libs.plugins.shadow)
    alias(libs.plugins.publish)
}

val revealKtVersion: String by project
group = "dev.limebeck"
version = revealKtVersion

buildTimeConfig {
    config {
        destination.set(project.layout.buildDirectory.get().asFile)
        objectName.set("RevealkConfig")
        packageName.set("dev.limebeck.revealkt")
        configProperties {
            val version by string(revealKtVersion)
            val kotlinVersion by string(libs.versions.kotlin.get())
        }
    }
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        java {
            withSourcesJar()
        }
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            // Configures a JavaExec task named "runJvm" and a Gradle distribution for the "main" compilation in this target
            executable {
                mainClass.set("dev.limebeck.application.ApplicationKt")
            }
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
                implementation(libs.kotlin.serialization)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(project(":reveal-kt:script-definition"))
                implementation(project(":reveal-kt:script-loader"))
//                implementation(libs.kotlin.serialization)
                implementation(libs.ktor.server.cio)
                implementation(libs.ktor.server.status.pages)
                implementation(libs.ktor.server.html.builder.jvm)
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
//                implementation(libs.kotlin.serialization)

                implementation(libs.kotlin.extensions)
                implementation(npm("reveal.js", "5.1.0"))
            }
        }

        val jsTest by getting
    }
}

val jvmProcessResources = tasks.named<Copy>("jvmProcessResources")

val jsCopyTask = tasks.register<Copy>("jsCopyTask") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
    into(jvmProcessResources.get().destinationDir.resolve("static"))
    excludes.add("*.zip")
    excludes.add("*.tar")
}

tasks.named("jvmJar") {
    dependsOn(jsCopyTask)
}

tasks.named("jvmTest") {
    dependsOn(jsCopyTask)
}

val shadow = tasks.getByName<ShadowJar>("shadowJar") {
    dependsOn(jsCopyTask) // make sure JS gets compiled first
    archiveClassifier.set("")
    mergeServiceFiles()
    mainClass = "dev.limebeck.application.ApplicationKt"
}

// Use the JVM sources JAR produced by withSourcesJar() for publishing
val jvmSourcesJar = tasks.named<Jar>("jvmSourcesJar")

publishing {
    publications {
        create<MavenPublication>("shadow") {
            artifact(shadow)
            artifact(jvmSourcesJar)
            artifactId = "revealkt-cli"
            pom {
                name.set("RevealKt kotlin-wrapper CLI for Reveal JS library")
                description.set("Kotlin cli module for RevealKt kotlin-wrapper for Reveal JS library")
                groupId = "dev.limebeck"
            }
        }
    }
}

//HACK: Publish CLI only
tasks.withType(PublishToMavenRepository::class.java).configureEach {
    enabled = (publication.name == "shadow")
}

tasks.withType(PublishToMavenLocal::class.java).configureEach {
    enabled = (publication.name == "shadow")
}

tasks.withType(Sign::class.java).configureEach {
    onlyIf { name.contains("Shadow", ignoreCase = true) }
}
