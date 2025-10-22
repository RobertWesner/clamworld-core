plugins {
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.6" // TODO: remove
}

group = "io.wesner.robert.cb1060.clamworldcore"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://maven.robert.wesner.io/repository/maven-public/")
    maven("https://maven.robert.wesner.io/repository/johnymuffin-maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.betamc:tsunami:1.0.4")
    implementation("commons-io:commons-io:2.20.0")
}

tasks.test {
    useJUnitPlatform()
}
tasks.processResources {
    filesMatching("plugin.yml") {
        expand(project.properties)
    }
}
kotlin {
    jvmToolchain(8)
}
