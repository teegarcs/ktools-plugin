package com.teegarcs.ktools

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@OptIn(ExperimentalCompilerApi::class)
class CLIPluginProcessor : CommandLineProcessor {

    companion object {
        private const val ENABLED_KEY = "enabled"
        private const val LOGGING_ENABLED_KEY = "logging_enabled"
        val ENABLED = CompilerConfigurationKey<Boolean>(ENABLED_KEY)
        val LOGGING_ENABLED = CompilerConfigurationKey<Boolean>(LOGGING_ENABLED_KEY)
    }

    override val pluginId: String = "com.teegarcs.ktools.ktools-plugin"
    override val pluginOptions: Collection<AbstractCliOption> =
        listOf(
            CliOption(
                ENABLED_KEY,
                "<true|false>",
                "Is plugin generally enabled",
                required = false
            ),
            CliOption(
                LOGGING_ENABLED_KEY,
                "<true|false>",
                "should log compositions",
                required = false
            )
        )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            ENABLED_KEY -> configuration.put(ENABLED, value.toBoolean())
            LOGGING_ENABLED_KEY -> configuration.put(LOGGING_ENABLED, value.toBoolean())
        }
    }
}