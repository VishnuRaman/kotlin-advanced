plugins {
    kotlin("jvm") version "2.0.0"
    id("com.google.devtools.ksp") version "2.0.0-1.0.21"
}

//group = "com.vishnuraman"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
    implementation(project(":builder-lib-annotations"))
    implementation(project(":builder-lib-impl"))
    ksp(project(":builder-lib-impl"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}