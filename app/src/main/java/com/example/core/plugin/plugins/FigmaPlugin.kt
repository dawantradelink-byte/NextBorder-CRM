package com.example.core.plugin.plugins

import com.example.core.ai.AiAgent
import com.example.core.plugin.AiPlugin
import com.example.core.plugin.PluginMetaData

class FigmaPlugin : AiPlugin {
    override val metaData = PluginMetaData(
        id = "com.figma.plugin",
        name = "Figma",
        version = "1.0",
        description = "Figma integration with Gmail account"
    )

    override fun onInitialize() {
    }

    override fun getProvidedAgents(): List<AiAgent> {
        return emptyList()
    }

    override fun onTerminate() {
    }
}
