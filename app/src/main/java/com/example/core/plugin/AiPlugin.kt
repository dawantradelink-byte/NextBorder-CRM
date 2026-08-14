package com.example.core.plugin

import com.example.core.ai.AiAgent

data class PluginMetaData(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val author: String = "NextBorder AIOS"
)

interface AiPlugin {
    val metaData: PluginMetaData
    fun onInitialize()
    fun getProvidedAgents(): List<AiAgent>
    fun onTerminate()
}

object PluginRegistry {
    private val plugins = mutableMapOf<String, AiPlugin>()

    fun registerPlugin(plugin: AiPlugin) {
        plugins[plugin.metaData.id] = plugin
        plugin.onInitialize()
        plugin.getProvidedAgents().forEach { agent ->
            com.example.core.ai.AiCoordinator.registerAgent(agent)
        }
    }

    fun unregisterPlugin(pluginId: String) {
        plugins[pluginId]?.let { plugin ->
            plugin.onTerminate()
            plugins.remove(pluginId)
        }
    }

    fun getInstalledPlugins(): List<PluginMetaData> = plugins.values.map { it.metaData }
}
