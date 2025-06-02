package com.teegarcs.ktools

import com.teegarcs.ktools.be.IRExtension
import com.teegarcs.ktools.fe.FIRRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class PluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2 = true

    override fun ExtensionStorage.registerExtensions(
        configuration: CompilerConfiguration
    ) {
        // Register our F/E Plugin(s)
        FirExtensionRegistrarAdapter.registerExtension(FIRRegistrar())

        // Register our B/E Plugin
        if (configuration.get(CLIPluginProcessor.ENABLED, false)) {
            // Message Collector is great for sending "messages" or logs to the output of your build process
            val messageCollector = configuration.get(
                CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY,
                MessageCollector.NONE
            )

            IrGenerationExtension.registerExtension(
                IRExtension(
                    messageCollector = messageCollector,
                    loggingEnabled = configuration.get(CLIPluginProcessor.LOGGING_ENABLED, false)
                )
            )
        }
    }
}