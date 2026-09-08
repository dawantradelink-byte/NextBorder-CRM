package com.example.core.plugin.plugins

import com.example.core.ai.AiAgent
import com.example.core.plugin.AiPlugin
import com.example.core.plugin.PluginMetaData

class OpalPlugin : AiPlugin {
    override val metaData = PluginMetaData(
        id = "com.opal.plugin",
        name = "Opal",
        version = "1.0",
        description = "Opal integration"
    )

    override fun onInitialize() {
    }

    override fun getProvidedAgents(): List<AiAgent> {
        return emptyList()
    }

    override fun onTerminate() {
    }
}
