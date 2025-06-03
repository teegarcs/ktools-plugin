package com.teegarcs.ktools

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class KToolsPlugin : KotlinCompilerPluginSupportPlugin {

    companion object {
        private const val EXTENSION_NAME = "KTools"
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

    override fun apply(target: Project) {
        target.extensions.create(EXTENSION_NAME, KToolsExtension::class.java)
        target.addCompilerRuntime()
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        return project.provider {
            listOf(
                SubpluginOption("enabled", project.getKToolsExtension().isEnabled.toString()),
                SubpluginOption("logging_enabled", project.getKToolsExtension().logCompositions.toString()),
            )
        }
    }

    override fun getCompilerPluginId(): String = "com.teegarcs.ktools.ktools-plugin"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "com.teegarcs.ktools",
        artifactId = "ktools-plugin",
        version = "0.1.0"
    )

    private fun Project.addCompilerRuntime() = afterEvaluate {
        dependencies {
            add("implementation", "com.teegarcs.ktools:ktools-runtime:0.1.0")
        }
    }

    private fun Project.getKToolsExtension() : KToolsExtension {
        return project.extensions.findByType(KToolsExtension::class.java)
            ?: project.extensions.create(EXTENSION_NAME, KToolsExtension::class.java)
    }
}
