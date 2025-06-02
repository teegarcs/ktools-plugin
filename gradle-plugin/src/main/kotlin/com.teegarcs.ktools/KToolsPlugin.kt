package com.teegarcs.ktools

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class KToolsPlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        TODO("Not yet implemented")
    }

    override fun getCompilerPluginId(): String {
        TODO("Not yet implemented")
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        TODO("Not yet implemented")
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        TODO("Not yet implemented")
    }

}