val revealKtVersion = "0.3.0"
group = "com.example"
version = "1.0.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.0.0-RC2"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("dev.limebeck:revealkt-script-definition:$revealKtVersion")
}

//sourceSets {}
