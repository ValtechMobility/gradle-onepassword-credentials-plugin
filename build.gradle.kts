import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm") version "1.9.22"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    `maven-publish`
    `signing`
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
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

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("1.1.1")
    debug.set(false)
    verbose.set(false)
    android.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    baseline.set(file("ktlint-baseline.xml"))
    filter {
        exclude {
            projectDir.toURI().relativize(it.file.toURI()).path.startsWith("build/")
        }
    }
}

dependencies {
    implementation(gradleApi())

    testImplementation("junit:junit:4.13.2")
    testImplementation(gradleTestKit())
    testImplementation("io.mockk:mockk:1.13.13")
}

java {
    withJavadocJar()
    withSourcesJar()
}

group = "io.github.valtechmobility"
version = "0.1.0"

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
                description.set(
                    "gradle-credentials-onepassword is a gradle repository credential integration for 1Password",
                )
                url.set("https://github.com/ValtechMobility/gradle-onepassword-credentials-plugin")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("vm")
                        name.set("Valtech Mobility")
                        email.set("sonatype@valtech-mobility.com")
                    }
                }
                scm {
                    connection.set("scm:git://github.com/ValtechMobility/gradle-onepassword-credentials-plugin.git")
                    developerConnection.set(
                        "scm:git://github.com/ValtechMobility/gradle-onepassword-credentials-plugin.git",
                    )
                    url.set("https://github.com/ValtechMobility/gradle-onepassword-credentials-plugin")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("MAVEN_USERNAME"))
            password.set(System.getenv("MAVEN_PASSWORD"))
        }
    }
}
