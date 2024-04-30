val revealKtVersion = "0.2.4"
group = "com.example"
version = "1.0.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.7.20"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("dev.limebeck:revealkt-script-definition:$revealKtVersion")
}

//sourceSets {}
