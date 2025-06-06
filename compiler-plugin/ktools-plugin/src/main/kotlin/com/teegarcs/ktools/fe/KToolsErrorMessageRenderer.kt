package com.teegarcs.ktools.fe

import com.teegarcs.ktools.fe.KToolsErrors.DISALLOWED_CALL_IN_SCOPE
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers

object KToolsErrorMessageRenderer : BaseDiagnosticRendererFactory() {

    override val MAP: KtDiagnosticFactoryToRendererMap by lazy {
        KtDiagnosticFactoryToRendererMap("KTools").also { map ->
            map.put(
                DISALLOWED_CALL_IN_SCOPE,
                "Invalid composable invocation: {0}(), within a restricted scope",
                CommonRenderers.STRING
            )
        }
    }

}