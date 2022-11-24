plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
    application
}

group = "dev.limebeck"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

val kotlinVersion: String by project
val serializationVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project
val kotlinxHtmlVersion: String by project

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
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
    mainClass.set("dev.limebeck.application.ServerKt")
//    mainModule.set("dev.limebeck.revealkt")
}

ktor {
    fatJar {
        archiveFileName.set("revealkt.jar")
    }
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
    excludes.add("*.zip")
    excludes.add("*.tar")
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}