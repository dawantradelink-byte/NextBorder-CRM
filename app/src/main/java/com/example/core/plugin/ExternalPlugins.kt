package com.example.core.plugin

import com.example.core.ai.AiAgent

class GoogleNanoBananaProPlugin : AiPlugin {
    override val metaData = PluginMetaData(
        id = "google_nano_banana_pro",
        name = "Google Nano Banana Pro",
        version = "1.0.0",
        description = "UI enhancement plugin for Google Nano Banana Pro",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {}

    override fun getProvidedAgents(): List<AiAgent> = emptyList()

    override fun onTerminate() {}
}

class OpalPlugin : AiPlugin {
    override val metaData = PluginMetaData(
        id = "opal",
        name = "Opal",
        version = "1.0.0",
        description = "UI enhancement plugin for Opal",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {}

    override fun getProvidedAgents(): List<AiAgent> = emptyList()

    override fun onTerminate() {}
}

class StitchPlugin : AiPlugin {
    override val metaData = PluginMetaData(
        id = "stitch",
        name = "Stitch",
        version = "1.0.0",
        description = "UI enhancement plugin for Stitch",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {}

    override fun getProvidedAgents(): List<AiAgent> = emptyList()

    override fun onTerminate() {}
}

class FigmaPlugin(private val accountEmail: String) : AiPlugin {
    override val metaData = PluginMetaData(
        id = "figma",
        name = "Figma",
        version = "1.0.0",
        description = "Figma integration with account: $accountEmail",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {}

    override fun getProvidedAgents(): List<AiAgent> = emptyList()

    override fun onTerminate() {}
}

class CanvaPlugin(private val accountEmail: String) : AiPlugin {
    override val metaData = PluginMetaData(
        id = "canva",
        name = "Canva",
        version = "1.0.0",
        description = "Canva integration with account: $accountEmail",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {}

    override fun getProvidedAgents(): List<AiAgent> = emptyList()

    override fun onTerminate() {}
}

object ExternalPluginInitializer {
    fun initializePlugins(email: String) {
        PluginRegistry.registerPlugin(GoogleNanoBananaProPlugin())
        PluginRegistry.registerPlugin(OpalPlugin())
        PluginRegistry.registerPlugin(StitchPlugin())
        PluginRegistry.registerPlugin(FigmaPlugin(email))
        PluginRegistry.registerPlugin(CanvaPlugin(email))
    }
}
