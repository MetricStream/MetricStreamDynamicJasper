plugins {
    java apply false
    `maven-publish` apply false
    signing apply false
}

group = "com.metricstream.jasper"
version = "1.0.0"

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}
