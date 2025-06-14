package com.teegarcs.ktools.be

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.path

class IRExtension(
    private val messageCollector: MessageCollector,
    private val loggingEnabled: Boolean
) : IrGenerationExtension {

    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext
    ) {

        val firstFilePath = moduleFragment.files.firstOrNull()?.path.orEmpty()
        if (firstFilePath.contains("src/test") || firstFilePath.contains("src/androidTest")) {
            return //we don't want to modify this module
        }

        //non-test, continue transformation
        moduleFragment.transform(
            KToolsTracker(
                pluginContext = pluginContext,
                messageCollector = messageCollector,
                shouldLogCompositions = loggingEnabled,
            ),
            TrackerContext() //the initial context of the IRTransformer
        )

    }
}