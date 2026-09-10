package com.example.core.plugin.plugins

import com.example.core.ai.AiAgent
import com.example.core.plugin.AiPlugin
import com.example.core.plugin.PluginMetaData

class StitchPlugin : AiPlugin {
    override val metaData: PluginMetaData = PluginMetaData(
        id = "stitch_ui_enhancer",
        name = "Stitch",
        version = "1.0.0",
        description = "Provides UI enhancement using Stitch seamlessly integrated styling.",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {
        com.example.core.log.AiosLogger.info(metaData.id, "Stitch plugin initialized.")
    }

    override fun getProvidedAgents(): List<AiAgent> {
        return emptyList()
    }

    override fun onTerminate() {
        com.example.core.log.AiosLogger.info(metaData.id, "Stitch plugin terminated.")
    }
}
