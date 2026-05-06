plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("io.github.dexman545.outlet")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.pauli.fyi/releases/")
}

val transitiveInclude: Configuration by configurations.creating {
    exclude(group = "com.mojang")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "org.jetbrains.kotlinx")
}

dependencies {
    val gameVersion: String by properties
    outlet.mcVersionRange = properties["fabricDependencyVersions"] as String

    //
    // Fabric configuration
    //
    minecraft("com.mojang:minecraft:$gameVersion")
//    println("FabricLoader: " + outlet.loaderVersion() + " " + outlet.fapiVersion())
//    modImplementation("net.fabricmc:fabric-loader:${outlet.loaderVersion()}")
//    modImplementation("net.fabricmc.fabric-api:fabric-api:${outlet.fapiVersion()}")
    modImplementation("net.fabricmc:fabric-loader:0.18.4"){
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.92.8+1.20.1")
    mappings(loom.officialMojangMappings())
    //
    // Kotlin libraries
    //
    val flkVersion = outlet.latestModrinthModVersion("fabric-language-kotlin", outlet.mcVersions())
    modImplementation("net.fabricmc:fabric-language-kotlin:$flkVersion"){
        exclude(group = "net.fabricmc.fabric-api")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.+")
    modImplementation("net.kyori:adventure-platform-fabric:${properties["adventureVersion"]}")
    include("net.kyori:adventure-platform-fabric:${properties["adventureVersion"]}")
    // Do not change the adventure to modimplementaion already in moyangs namespace.

    //
    // Silk configuration (optional)
    //
    val silkVersion = properties["silkVersion"] as String
    println("Silk: $silkVersion")
    modImplementation("net.silkmc:silk-core:$silkVersion"){
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation("net.silkmc:silk-commands:$silkVersion"){
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation("net.silkmc:silk-nbt:$silkVersion"){
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation("net.silkmc:silk-network:$silkVersion"){
        exclude(group = "net.fabricmc.fabric-api")
    }


    //
    // Permissions configuration (optional)
    //
    val usePermissions = properties["usePermissions"] as String == "true"
    if (usePermissions) {
        modImplementation("me.lucko:fabric-permissions-api:0.3.3"){
            exclude(group = "net.fabricmc.fabric-api")
        }
        include("me.lucko:fabric-permissions-api:0.3.3")
    }

    //
    // Configuration
    //
    transitiveInclude(implementation("org.yaml:snakeyaml:2.5")!!)

    // Add all non-mod dependencies to the jar
    include("de.miraculixx:mc-commons:1.0.1")

}

tasks.processResources {
    println("-----" + outlet.mcVersionRange + " - ${properties["version"]}")
    filesMatching("fabric.mod.json") {
        val modrinthSlug = properties["modrinthProjectId"] as? String ?: properties["modid"] as String
        expand(
            mapOf(
                "modid" to properties["modid"] as String,
                "version" to properties["version"] as String,
                "name" to properties["projectName"] as String,
                "description" to properties["description"],
                "author" to properties["author"] as String,
                "license" to properties["licence"] as String,
                "modrinth" to modrinthSlug,
                "environment" to properties["environment"] as String,
                "mcversion" to outlet.mcVersionRange,
            )
        )
    }
}
