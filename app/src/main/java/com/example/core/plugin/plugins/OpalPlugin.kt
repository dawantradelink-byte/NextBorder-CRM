package com.example.core.plugin.plugins

import com.example.core.ai.AiAgent
import com.example.core.plugin.AiPlugin
import com.example.core.plugin.PluginMetaData

class OpalPlugin : AiPlugin {
    override val metaData: PluginMetaData = PluginMetaData(
        id = "opal_ui_enhancer",
        name = "Opal",
        version = "1.0.0",
        description = "Provides UI enhancement using Opal design elements.",
        author = "NextBorder AIOS"
    )

    override fun onInitialize() {
        com.example.core.log.AiosLogger.info(metaData.id, "Opal plugin initialized.")
    }

    override fun getProvidedAgents(): List<AiAgent> {
        return emptyList()
    }

    override fun onTerminate() {
        com.example.core.log.AiosLogger.info(metaData.id, "Opal plugin terminated.")
    }
}
