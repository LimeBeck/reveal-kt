import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
}

val revealKtVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project

group = "dev.limebeck"
version = revealKtVersion

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

java {
    withSourcesJar()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinCoroutinesVersion}") {
        version {
            strictly(kotlinCoroutinesVersion)
        }
    }

    api("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    api(libs.qrcode)

    implementation(project(":reveal-kt:lib-dsl"))
    implementation(project(":reveal-kt:script-definition"))
}

sourceSets {
    test {}
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

val stubJavaDocJar by tasks.registering(Jar::class) {
    archiveClassifier.value("javadoc")
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
        create<MavenPublication>("scriptLoader") {
            from(components["java"])
            artifact(stubJavaDocJar)
            artifactId = "revealkt-script-loader"
            pom {
                name.set("RevealKt kotlin-wrapper script loader for Reveal JS library")
                description.set("Kotlin script loader module for RevealKt kotlin-wrapper for Reveal JS library")
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