import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.versions)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.publish.base)
}

val revealKtVersion: String by project
group = "dev.limebeck"
version = revealKtVersion

subprojects {
    group = rootProject.group
    version = rootProject.version

//    configurations.configureEach {
//        resolutionStrategy.failOnVersionConflict()
//    }

    plugins.withId("java") {
        tasks.withType<JavaCompile>().configureEach {
            options.release.set(11)
        }
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.vanniktech.maven.publish")

    publishing {
        repositories {
            maven {
                name = "MainRepo"
                url = uri(
                    System.getenv("REPO_URI")
                        ?: project.findProperty("repo.uri") as String
                )

                val repoUsername = System.getenv("REPO_USERNAME")
                    ?: project.findProperty("repo.username") as String?
                val repoPassword = System.getenv("REPO_PASSWORD")
                    ?: project.findProperty("repo.password") as String?

                if (repoUsername != null && repoPassword != null) {
                    credentials {
                        username = repoUsername
                        password = repoPassword
                    }
                }
            }
        }
    }

    mavenPublishing {
        publishToMavenCentral()

        // Local publication checks do not need release signing keys.
        if (!providers.gradleProperty("unsignedPublication").isPresent) {
            signAllPublications()
        }

        pom {
            url = "https://github.com/LimeBeck/reveal-kt"
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

dokka {
    moduleName.set("RevealKt kotlin-wrapper CLI for Reveal JS library")

    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
    }

    dokkaSourceSets.configureEach {
        includes.from("README.MD")
    }

    pluginsConfiguration.html {
        footerMessage.set("(c) LimeBeck.Dev")
    }
}

dependencies {
    dokka(project(":reveal-kt:lib-dsl"))
    dokka(project(":reveal-kt:script-definition"))
    dokka(project(":reveal-kt:script-loader"))
}

// A complete static website, including examples compiled by the packaged CLI.
// Serve/publish build/site, never the unrendered docs source directory.
tasks.register<Exec>("buildSite") {
    group = "documentation"
    description = "Build the documentation site and render its RevealKt examples"
    dependsOn(":reveal-kt:app:shadowJar")
    val cli = layout.projectDirectory.file("reveal-kt/app/build/libs/revealkt.jar")
    val site = layout.buildDirectory.dir("site")
    inputs.file(cli)
    inputs.dir("docs")
    inputs.dir("reveal-kt/app/src/jvmMain/resources/examples")
    inputs.file("scripts/build-site.py")
    outputs.dir(site)
    commandLine("python3", "scripts/build-site.py", "--cli", cli.asFile.absolutePath,
        "--output", site.get().asFile.absolutePath)
}
