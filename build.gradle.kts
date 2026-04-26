group = "com.metricstream.jasper"
version = "1.0.0"

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Specification-Title" to project.name,
                "Specification-Version" to project.version,
                "Bundle-Name" to project.name,
                "Bundle-Version" to project.version,
                "Created-By" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
                "Build-Jdk" to System.getProperty("java.version"),
                "Build-Jdk-Spec" to System.getProperty("java.specification.version"),
                "Build-Timestamp" to java.time.Instant.now().toString()
            )
        }
    }
}
