package com.teegarcs.ktools.fe

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension

class KToolsCheckerExtension(
    session: FirSession
) : FirAdditionalCheckersExtension(session) {

    override val expressionCheckers = object : ExpressionCheckers() {

        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
            KToolsRestrictedScopeChecker
        )
    }
}