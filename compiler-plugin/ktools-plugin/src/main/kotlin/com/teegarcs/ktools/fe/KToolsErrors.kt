package com.teegarcs.ktools.fe

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.Renderers
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.psi.KtElement

object KToolsErrors : BaseDiagnosticRendererFactory() {

    val DISALLOWED_CALL_IN_SCOPE by error1<KtElement, String>()

    override val MAP: KtDiagnosticFactoryToRendererMap by lazy {
        KtDiagnosticFactoryToRendererMap("KTools").apply {
            put(
                DISALLOWED_CALL_IN_SCOPE,
                "Invalid composable invocation: {0}, within a restricted scope",
                Renderers.TO_STRING
            )
        }
    }

    init {
        RootDiagnosticRendererFactory.registerFactory(KToolsErrors)
    }
}