package com.teegarcs.ktools.fe

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class FIRRegistrar : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +::KToolsCheckerExtension
    }
}