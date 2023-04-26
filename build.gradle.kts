import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

group = "com.valtechmobility.gradle.credentials.onepassword"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
}

tasks.withType<Test> {
    testLogging {
        showStandardStreams = true
        events("skipped", "failed")
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    testImplementation("junit:junit:4.13.2")
    testImplementation(gradleTestKit())
    testImplementation("io.mockk:mockk:1.12.3")
}