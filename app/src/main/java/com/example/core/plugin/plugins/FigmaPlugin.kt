package com.example.core.plugin.plugins

import com.example.core.ai.AiAgent
import com.example.core.plugin.AiPlugin
import com.example.core.plugin.PluginMetaData

class FigmaPlugin : AiPlugin {
    override val metaData: PluginMetaData = PluginMetaData(
        id = "figma_integration",
        name = "Figma",
        version = "1.0.0",
        description = "Figma integration for UI designs.",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {
        com.example.core.log.AiosLogger.info(metaData.id, "Figma plugin initialized.")
    }

    override fun getProvidedAgents(): List<AiAgent> {
        return emptyList()
    }

    override fun onTerminate() {
        com.example.core.log.AiosLogger.info(metaData.id, "Figma plugin terminated.")
    }
}
