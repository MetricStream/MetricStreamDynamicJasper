plugins {
    java
    `maven-publish`
    signing
}

description = "Maintained fork of DynamicJasper originally created by FDV Solutions. Library for generating JasperReports dynamically at runtime."

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configurations.all {
    resolutionStrategy {
        force(
            libs.jackson.core,
            libs.poi.ooxml,
            libs.jasperreports
        )
    }
}

dependencies {
    // JasperReports core dependencies
    implementation(libs.jasperreports)
    implementation(libs.jasperreports.chart.customizers)
    implementation(libs.jasperreports.charts)
    implementation(libs.jasperreports.excel.poi)
    implementation(libs.jasperreports.hibernate)
    implementation(libs.jasperreports.javaflow)
    implementation(libs.jasperreports.jaxen)
    implementation(libs.jasperreports.jdt)
    implementation(libs.jasperreports.pdf)
    implementation(libs.barbecue)
    implementation(libs.jasperreports.servlets)
    implementation(libs.slf4j.api)
    runtimeOnly(libs.jackson.core)

    // Core dependencies
    implementation(libs.commons.beanutils2)
    implementation(libs.xmlgraphics.commons)

    // Provided dependencies
    implementation(libs.servlet.api)
    implementation(project(":modules:ms-dynamic-jasper-core-fonts"))

    // Test dependencies
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.poi.ooxml)
    testImplementation(libs.jaxen)
    testImplementation(libs.jxl) {
        exclude(group = "log4j", module = "log4j")
    }
    testImplementation(libs.xalan)
    testImplementation(libs.mysql.connector.j)
    testImplementation(libs.hsqldb)
    testImplementation(libs.hibernate.core)
    testImplementation(libs.jakarta.transaction.api)
    testImplementation(libs.httpunit) { isTransitive = false }
    testImplementation(project(":modules:ms-dynamic-jasper-test-fonts"))
    testImplementation(libs.spring.core)
    testImplementation(libs.spring.web)
    testImplementation(libs.spring.test)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "ms-dynamic-jasper"
            from(components["java"])

            pom {
                name.set("ms-dynamic-jasper")
                description.set(project.description)
                url.set("https://github.com/MetricsStream/MetricsStreamDynamicJasper")

                licenses {
                    license {
                        name.set("GNU Library or Lesser General Public License (LGPL)")
                        url.set("https://www.gnu.org/licenses/lgpl.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("leonelfmfilho")
                        name.set("Leonel Francisco Martins Filho")
                        email.set("leonel.martins@nostrum.tech")
                        organization.set("Nostrum Tech")
                        organizationUrl.set("http://www.nostrum.tech/")
                        roles.addAll(listOf("Project lead", "Java Developer"))
                        timezone.set("-3")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/MetricStream/MetricStreamDynamicJasper.git")
                    developerConnection.set("scm:git:ssh://github.com:MetricStream/MetricStreamDynamicJasper.git")
                    url.set("https://github.com/MetricStream/MetricStreamDynamicJasper")
                }

                issueManagement {
                    system.set("GitHub Tracker")
                    url.set("https://github.com/MetricStream/MetricStreamDynamicJasper/issues")
                }

                inceptionYear.set("2006")

                organization {
                    name.set("Nostrum Tech")
                    url.set("http://www.nostrum.tech/")
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
