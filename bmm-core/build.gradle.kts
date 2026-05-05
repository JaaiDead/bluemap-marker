
plugins {
    `core-script`
    `adventure-script`
    id("net.fabricmc.fabric-loom-remap")
}

dependencies {
    val gameVersion: String by properties
    mappings(loom.officialMojangMappings())
    minecraft("com.mojang", "minecraft", gameVersion)
}
