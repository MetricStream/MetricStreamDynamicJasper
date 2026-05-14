rootProject.name = "metricstream-dynamic-jasper"

plugins {
    id("com.gradleup.nmcp.settings") version "1.4.4"
}

nmcpSettings {
    centralPortal {
        username = providers.gradleProperty("sonatypeUsername").getOrElse("")
        password = providers.gradleProperty("sonatypePassword").getOrElse("")
        publishingType = "USER_MANAGED"
    }
}

include(
    "modules:ms-dynamic-jasper",
    "modules:ms-dynamic-jasper-core-fonts",
    "modules:ms-dynamic-jasper-test-fonts"
)
