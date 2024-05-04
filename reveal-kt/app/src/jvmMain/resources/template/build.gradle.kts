val revealKtVersion = "{{version}}"

group = "dev.limebeck"
version = "1.0.0"

plugins {
    kotlin("jvm") version "{{kotlinVersion}}"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.limebeck:revealkt-script-definition:$revealKtVersion")
}

//sourceSets {}
