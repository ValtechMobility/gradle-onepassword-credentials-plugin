import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    `java-library`
    kotlin("jvm") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    `maven-publish`
}

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

    testImplementation("junit:junit:4.13.2")
    testImplementation(gradleTestKit())
    testImplementation("io.mockk:mockk:1.12.3")
}

group = "com.valtechmobility"
version = "0.0.2"

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = "gradle-credentials-onepassword"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("gradle-credentials-onepassword")
                description.set("gradle-credentials-onepassword is a gradle repository credential integration for 1Password")
                url.set("https://github.com/ValtechMobility/gradle-onepassword-credentials-plugin")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("janphkre")
                        name.set("Jan Phillip Kretzschmar")
                        email.set("janphkre@gmx.de")
                    }
                }
                scm {
                    connection.set("scm:git://github.com/ValtechMobility/gradle-onepassword-credentials-plugin.git")
                    developerConnection.set("scm:git://github.com/ValtechMobility/gradle-onepassword-credentials-plugin.git")
                    url.set("https://github.com/ValtechMobility/gradle-onepassword-credentials-plugin")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/ValtechMobility/gradle-onepassword-credentials-plugin")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
