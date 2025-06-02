@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package com.teegarcs.ktools.be

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.serialization.mangle.ir.isAnonymous
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isAnonymousFunction
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class KToolsTracker(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector,
    private val shouldLogCompositions: Boolean
) : IrTransformer<TrackerContext>() {

    companion object {
        val loggingFunction = CallableId(
            FqName("com.teegarcs.ktools"),
            Name.identifier("TrackComposition")
        )

        val currentTimeMillisFunction = CallableId(
            FqName("com.teegarcs.ktools"),
            Name.identifier("getCurrentTime")
        )
    }

    private val loggingSymbol by lazy {
        pluginContext.referenceFunctions(loggingFunction).firstOrNull()?.owner?.symbol
    }

    private val getTimeMillisSymbol by lazy {
        pluginContext.referenceFunctions(currentTimeMillisFunction).firstOrNull()?.owner?.symbol
    }

    // visiting the file only to track the current file we are in and add it to tracker context
    override fun visitFile(declaration: IrFile, data: TrackerContext): IrFile {
        return super.visitFile(declaration, data.copy(currentFile = declaration.fileEntry))
    }

    // visiting the function to add the function to the tracker context
    override fun visitFunction(declaration: IrFunction, data: TrackerContext): IrStatement {
        return super.visitFunction(declaration, data.copy(parentFunction = declaration))
    }

    // visiting calls to add the call declaration to the tracker context.
    override fun visitCall(expression: IrCall, data: TrackerContext): IrElement {
        return super.visitCall(expression, data.copy(nearestCall = expression))
    }

    // We want to add the tracking of compositions to function block bodies.
    override fun visitBlockBody(body: IrBlockBody, data: TrackerContext): IrBody {
        if (body.hasComposables()) {
            val composableName = data.generateLog()
            val message = "Tracking Composable: ${composableName}"
            messageCollector.report(
                CompilerMessageSeverity.INFO, // to see this message during compilation add --info to task run
                message,
                data.currentFile?.toLocation(body.startOffset)
            )

            buildTracker(composableName)?.let {
                body.statements.add(
                    0,
                    it
                ) // add our TrackComposition function to the first line of the block body
            }
        }

        return super.visitBlockBody(body, data)
    }

    private fun buildTracker(log: String): IrCallImpl? {
        return loggingSymbol?.let {
            val getTimeMillisCall = IrCallImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = pluginContext.irBuiltIns.longType,
                symbol = getTimeMillisSymbol!!
            )

            IrCallImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = pluginContext.irBuiltIns.unitType,
                symbol = it,
            ).apply {
                putValueArgument(0, log.toIrConst(pluginContext.irBuiltIns.stringType))
                putValueArgument(
                    1,
                    shouldLogCompositions.toIrConst(pluginContext.irBuiltIns.booleanType)
                )
                putValueArgument(2, getTimeMillisCall)
            }
        }
    }

}

/**
 *  Utility to check if a call being made is a Composable function.
 */
private fun IrCall.isComposable(): Boolean {
    val callOwner = symbol.owner
    val callName = callOwner.name
    return !callName.isSpecial && callOwner.isComposable()
}

/**
 * Utility to check if a function definition is a Composable by checking if the function has the
 * Composable Annotation.
 */
private fun IrFunction.isComposable(): Boolean =
    hasAnnotation(FqName("androidx.compose.runtime.Composable"))

/**
 * Utility to check if a block body has any Compsable calls/statements within it.
 */
private fun IrBlockBody.hasComposables(): Boolean =
    statements.any { it is IrCall && it.isComposable() }

/**
 * When sending messages with the MessageCollector, providing a location that to the message
 * provides context about what was being visited at the time of the message being sent.
 */
private fun IrFileEntry.toLocation(startOffset: Int): CompilerMessageLocation? {
    return CompilerMessageLocation.create(
        path = name,
        line = getLineNumber(startOffset),
        column = getColumnNumber(startOffset),
        lineContent = null
    )
}

/**
 * To Track the Composable we want to track the function name based on the nearest function that has
 * a name that means something. If the block we are in is an anonymous function that belongs to another
 * composable statement, we want to leverage that composable statement as the name. For example:
 * Button(){
 *   //in this block. We will use name Button
 *   Text("My Button")
 * }
 */
private fun TrackerContext.generateLog(): String {
    return if (parentFunction?.isAnonymousFunction == true || parentFunction?.name?.isAnonymous == true) {
        nearestCall?.symbol?.owner?.name?.asString().orEmpty()
    } else {
        parentFunction?.name?.asString().orEmpty()
    }
}

data class TrackerContext(
    val currentFile: IrFileEntry? = null,
    val parentFunction: IrFunction? = null,
    val nearestCall: IrCall? = null
)