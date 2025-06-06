package com.teegarcs.ktools.fe

import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.psi.KtElement

object KToolsErrors {

    val DISALLOWED_CALL_IN_SCOPE by error1<KtElement, String>()

    init {
        RootDiagnosticRendererFactory.registerFactory(KToolsErrorMessageRenderer)
    }
}