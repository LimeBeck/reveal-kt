import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
    id("dev.limebeck.build-time-config") version "1.1.2"
}

val revealKtVersion: String by project
group = "dev.limebeck"
version = revealKtVersion

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

val kotlinVersion: String by project
val serializationVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project
val kotlinxHtmlVersion: String by project

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
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        java {
            withSourcesJar()
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
                    enabled = true
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":reveal-kt:lib-dsl"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${serializationVersion}")
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
                implementation("io.ktor:ktor-server-cio:$ktorVersion")
                implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("org.slf4j:slf4j-api:2.0.4")
                implementation("com.github.ajalt.clikt:clikt:3.5.0")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.346")
                implementation(npm("reveal.js", "4.4.0"))
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("dev.limebeck.application.ApplicationKt")
}

val copyJsTask = tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
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
    dependsOn(copyJsTask) // make sure JS gets compiled first
    archiveClassifier.set("")
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = "dev.limebeck.application.ServerKt"
    }
    finalizedBy(stubJavaDocJar)
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
    onlyIf {
        it.name.contains("shadow", ignoreCase = true)
    }
}

tasks.withType<PublishToMavenLocal>().configureEach {
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