package com.teegarcs.ktools.fe

import com.teegarcs.ktools.fe.KToolsErrors.DISALLOWED_CALL_IN_SCOPE
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.containingClassLookupTag
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.declarations.utils.isOperator
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.plugin.createConeType
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.util.OperatorNameConventions

object KToolsRestrictedScopeChecker : FirFunctionCallChecker(MppCheckerKind.Common) {

    private val restrictedScopeAnnotation =
        ClassId(FqName("com.teegarcs.ktools"), Name.identifier("RestrictedScope"))
    private val composableAnnotation =
        ClassId(FqName("androidx.compose.runtime"), Name.identifier("Composable"))

    // Cache to avoid re-calculating if a scope is restricted for every call inside it.
    private val scopeRestrictionCache = mutableMapOf<FirAnonymousFunction, ConeKotlinType?>()

    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter
    ) {
        // Find the nearest enclosing lambda.
        val enclosingLambda = context.containingDeclarations
            .lastOrNull { it is FirAnonymousFunction } as? FirAnonymousFunction ?: return

        // Check if the lambda's receiver is restricted (using a cache for efficiency).
        val restrictedReceiverType = getRestrictedReceiverType(enclosingLambda, context) ?: return

        // Resolve the function being called.
        val calleeSymbol =
            expression.toResolvedCallableSymbol() as? FirNamedFunctionSymbol ?: return

        // Allow non-@Composable functions unconditionally.
        if (!calleeSymbol.hasAnnotation(composableAnnotation, context.session)) {
            return
        }

        // Allow calls to the lambda parameter itself (e.g., the `CustomScope.func()` part).
        if (isCallToLambdaParameter(expression, calleeSymbol, context)) {
            return
        }

        // Check if the @Composable call is a member of the restricted scope.
        if (!isCallAllowed(calleeSymbol, restrictedReceiverType, context)) {
            reporter.reportOn(
                source = expression.calleeReference.source,
                factory = DISALLOWED_CALL_IN_SCOPE, // Replace with your actual error factory
                a = calleeSymbol.name.asString(),
                context = context,
                positioningStrategy = SourceElementPositioningStrategies.DEFAULT,
            )
        }
    }

    private fun getRestrictedReceiverType(
        lambda: FirAnonymousFunction,
        context: CheckerContext
    ): ConeKotlinType? {
        return scopeRestrictionCache.getOrPut(lambda) {
            val receiverType =
                lambda.receiverParameter?.typeRef?.coneTypeOrNull ?: return@getOrPut null
            val receiverClass =
                receiverType.toRegularClassSymbol(context.session) ?: return@getOrPut null
            if (receiverClass.hasAnnotation(restrictedScopeAnnotation, context.session)) {
                receiverType
            } else {
                null
            }
        }
    }

    private fun isCallToLambdaParameter(
        expression: FirFunctionCall,
        calleeSymbol: FirNamedFunctionSymbol,
        context: CheckerContext
    ): Boolean {
        // Robustly check for `invoke` calls on a lambda parameter.
        val isInvokeCall =
            calleeSymbol.isOperator && calleeSymbol.name == OperatorNameConventions.INVOKE
        if (!isInvokeCall) return false

        // Check if the receiver of the 'invoke' is a value parameter (the `func` in `func()`).
        val receiverSymbol = expression.dispatchReceiver?.toResolvedCallableSymbol(context.session)
        return receiverSymbol is FirValueParameterSymbol
    }

    private fun isCallAllowed(
        calleeSymbol: FirNamedFunctionSymbol,
        expectedReceiverType: ConeKotlinType,
        context: CheckerContext
    ): Boolean {
        // A call is allowed if its defining class is a subtype of the restricted scope's receiver type.
        val ownerClassId = calleeSymbol.containingClassLookupTag()?.classId ?: return false
        val ownerType = ownerClassId.createConeType(context.session)

        return ownerType.isSubtypeOf(expectedReceiverType, context.session)
    }
}