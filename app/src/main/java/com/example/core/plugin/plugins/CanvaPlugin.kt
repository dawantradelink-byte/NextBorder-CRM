package com.example.core.plugin.plugins

import com.example.core.ai.AiAgent
import com.example.core.plugin.AiPlugin
import com.example.core.plugin.PluginMetaData

class CanvaPlugin : AiPlugin {
    override val metaData: PluginMetaData = PluginMetaData(
        id = "canva_integration",
        name = "Canva",
        version = "1.0.0",
        description = "Canva integration for graphics and UI assets.",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {
        com.example.core.log.AiosLogger.info(metaData.id, "Canva plugin initialized.")
    }

    override fun getProvidedAgents(): List<AiAgent> {
        return emptyList()
    }

    override fun onTerminate() {
        com.example.core.log.AiosLogger.info(metaData.id, "Canva plugin terminated.")
    }
}
