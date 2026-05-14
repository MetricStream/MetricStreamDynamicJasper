plugins {
    java
    `maven-publish`
    signing
}

description = "Test fonts for MetricStream DynamicJasper (Colonna MT)"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    compileOnly(libs.jasperreports)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "ms-dynamic-jasper-test-fonts"
            from(components["java"])

            pom {
                name.set("ms-dynamic-jasper-test-fonts")
                description.set(project.description)
                url.set("https://github.com/MetricStream/MetricStreamDynamicJasper")

                licenses {
                    license {
                        name.set("GNU Library or Lesser General Public License (LGPL)")
                        url.set("https://www.gnu.org/licenses/lgpl.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        name.set("Prasadu Babu Dandu")
                        email.set("prasadbabu@metricstream.com")
                        organization.set("MetricStream Inc")
                        organizationUrl.set("https://metricstream.com/")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/MetricStream/MetricStreamDynamicJasper.git")
                    developerConnection.set("scm:git:ssh://github.com:MetricStream/MetricStreamDynamicJasper.git")
                    url.set("https://github.com/MetricStream/MetricStreamDynamicJasper")
                }
            }
        }
    }

    repositories {
        maven {
            name = "Central"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
            credentials {
                username = project.findProperty("centralUsername") as String? ?: System.getenv("CENTRAL_USERNAME")
                password = project.findProperty("centralPassword") as String? ?: System.getenv("CENTRAL_PASSWORD")
            }
        }
    }
}
