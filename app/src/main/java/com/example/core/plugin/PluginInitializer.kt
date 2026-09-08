package com.example.core.plugin

import com.example.core.plugin.plugins.*

object PluginInitializer {
    fun initializePlugins() {
        PluginRegistry.registerPlugin(GoogleNanoPlugin())
        PluginRegistry.registerPlugin(BananaProPlugin())
        PluginRegistry.registerPlugin(OpalPlugin())
        PluginRegistry.registerPlugin(StitchPlugin())
        PluginRegistry.registerPlugin(FigmaPlugin())
        PluginRegistry.registerPlugin(CanvaPlugin())
    }
}
