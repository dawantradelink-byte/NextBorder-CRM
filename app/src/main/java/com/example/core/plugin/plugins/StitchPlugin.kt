package com.example.core.plugin.plugins

import com.example.core.ai.AiAgent
import com.example.core.plugin.AiPlugin
import com.example.core.plugin.PluginMetaData

class StitchPlugin : AiPlugin {
    override val metaData = PluginMetaData(
        id = "com.stitch.plugin",
        name = "Stitch",
        version = "1.0",
        description = "Stitch integration"
    )

    override fun onInitialize() {
    }

    override fun getProvidedAgents(): List<AiAgent> {
        return emptyList()
    }

    override fun onTerminate() {
    }
}
