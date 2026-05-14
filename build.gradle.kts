import java.time.Instant

plugins {
    base
    `java-library`
    `maven-publish`
    signing
}

group = "com.metricstream.jasper"
version = "1.0.0-alpha.0"

repositories {
    mavenCentral()
}

subprojects {

    apply(plugin = "maven-publish")
    apply(plugin = "java-library")
    apply(plugin = "signing")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    afterEvaluate {
        val mavenJava = publishing.publications.findByName("mavenJava")
        if (mavenJava != null) {
            signing {
                val signingKeyFile: String? by project
                val signingPassword: String? by project
                if (signingKeyFile != null) {
                    val keyContent = file(signingKeyFile!!).readText()
                    useInMemoryPgpKeys(keyContent, signingPassword)
                }
                sign(mavenJava)
            }
        }
    }

    tasks.withType<Jar> {
        manifest {
            attributes(mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Specification-Title" to project.name,
                "Specification-Version" to project.version,
                "Bundle-Name" to project.name,
                "Bundle-Version" to project.version,
                "Created-By" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
                "Build-Jdk" to System.getProperty("java.version"),
                "Build-Jdk-Spec" to System.getProperty("java.specification.version"),
                "Build-Timestamp" to Instant.now().toString()
            ))
        }
    }
}
