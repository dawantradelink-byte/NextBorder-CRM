package com.example.core.plugin.plugins

import com.example.core.ai.AiAgent
import com.example.core.plugin.AiPlugin
import com.example.core.plugin.PluginMetaData

class BananaProPlugin : AiPlugin {
    override val metaData = PluginMetaData(
        id = "com.banana.pro",
        name = "Banana Pro",
        version = "1.0",
        description = "Banana Pro integration"
    )

    override fun onInitialize() {
    }

    override fun getProvidedAgents(): List<AiAgent> {
        return emptyList()
    }

    override fun onTerminate() {
    }
}
