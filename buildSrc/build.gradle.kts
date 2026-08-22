plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    // Real markdown parsing for the validator's "markdown that fails to parse"
    // check, rather than a hand-rolled regex scan over 60+ hand-authored files.
    implementation("org.commonmark:commonmark:0.22.0")
}
