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

    compileOnly(local.adventurelevel)
    compileOnly(local.economy)
    compileOnly(local.helper)
    compileOnly(local.orientation)
    compileOnly(local.towny)
    compileOnly(local.wakame.api)
    compileOnly(local.wakame.common)

    /* internal */

    implementation(project(":common"))

    // network
    implementation(local.nettowaku)

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
        register("AdventureLevel") {
            required = false
            load = RelativeLoadOrder.OMIT // 懒加载 class
        }
        register("Economy") {
            required = false
            load = RelativeLoadOrder.OMIT // 懒加载 class
        }
        register("Orientation") {
            required = false
            load = RelativeLoadOrder.OMIT // 懒加载 class
        }
        register("Towny") {
            required = false
            load = RelativeLoadOrder.OMIT // 懒加载 class
        }
        register("Wakame") {
            required = false
            load = RelativeLoadOrder.BEFORE
        }
    }
}