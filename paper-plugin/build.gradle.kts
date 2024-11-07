import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder

plugins {
    id("yuuai-conventions.commons")
    id("yuuai-koin")
    id("nyaadanbou-conventions.repositories")
    id("nyaadanbou-conventions.copy-jar")
    alias(libs.plugins.pluginyml.paper)
}

group = "cc.mewcraft.yuuai"
version = "1.0.0-SNAPSHOT"

dependencies {
    // server
    compileOnly(local.paper)

    /* external */

    compileOnly(local.helper)
    compileOnly(local.economy.api)

    /* internal */

    // configurate
    implementation(platform(libs.bom.configurate.yaml))
    implementation(platform(libs.bom.configurate.kotlin))

    // cloud
    implementation(platform(libs.bom.cloud.paper))
    implementation(platform(libs.bom.cloud.kotlin)) {
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
    }

    // scoreboard
    implementation(local.scoreboardlibrary.api)
    implementation(local.scoreboardlibrary.implementation)
    implementation(local.scoreboardlibrary.extra.kotlin)
    implementation(variantOf(local.scoreboardlibrary.adapter.modern) { classifier("mojmap") })
}

tasks {
    copyJar {
        environment = "paper"
        jarFileName = "yuuai-${project.version}.jar"
    }
}

paper {
    main = "cc.mewcraft.yuuai.YuuaiPlugin"
    name = "yuuai"
    version = "${project.version}"
    description = project.description
    apiVersion = "1.21"
    author = "g2213swo"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    serverDependencies {
        register("Economy") {
            required = false
            load = RelativeLoadOrder.BEFORE
        }
    }
}