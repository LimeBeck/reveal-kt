plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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
                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.346")
//                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.346")
//                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.346")
//                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.3-pre.346")
                implementation(npm("reveal.js", "4.4.0", generateExternals = true))
            }
        }
        val jsTest by getting
    }
}

application {
    mainClass.set("dev.limebeck.application.ServerKt")
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