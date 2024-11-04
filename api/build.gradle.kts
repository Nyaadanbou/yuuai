plugins {
    id("example-conventions.commons")
    id("nyaadanbou-conventions.repositories")
    `maven-publish`
}

group = "cc.mewcraft.example"
version = "1.0.0-SNAPSHOT"

dependencies {
    // server
    compileOnly(libs.server.paper)
}

publishing {
    repositories {
        maven("https://repo.mewcraft.cc/private") {
            credentials {
                username = providers.gradleProperty("nyaadanbou.mavenUsername").orNull
                password = providers.gradleProperty("nyaadanbou.mavenPassword").orNull
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}