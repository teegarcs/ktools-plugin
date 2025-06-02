package com.teegarcs.ktools.fe

import com.teegarcs.ktools.fe.KToolsErrors.DISALLOWED_CALL_IN_SCOPE
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.containingClassLookupTag
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.types.receiverType
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class KToolsRestrictedScopeChecker(
    private val session: FirSession
) : FirFunctionCallChecker(MppCheckerKind.Common) {

    companion object {
        private val restrictedScopeAnnotation = ClassId(
            FqName("com.teegarcs.ktools"),
            Name.identifier("RestrictedScope")
        )
        private val composableAnnotation = ClassId(
            FqName("androidx.compose.runtime"),
            Name.identifier("Composable")
        )
    }

    @OptIn(SymbolInternals::class)
    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter
    ) {

        // Find the nearest enclosing anonymous function/lambda
        val enclosingLambda = context.containingDeclarations
            .filterIsInstance<FirAnonymousFunction>()
            .lastOrNull() ?: return // Not inside lambda, nothing to check return.

        // Get its type and check for an extension Receiver
        val lambdaType = enclosingLambda.typeRef.coneTypeOrNull ?: return
        val expectedReceiverType = lambdaType.receiverType(session)
            ?: return // we only care about anonymous functions with a receiver
        val expectedReceiverClassId = expectedReceiverType.classId ?: return

        // get the FirClass for the receiver type and check if it has the @RestrictedScope
        val receiverClassSymbol =
            session.symbolProvider.getClassLikeSymbolByClassId(expectedReceiverClassId) as? FirRegularClassSymbol
                ?: return

        val receiverClass = receiverClassSymbol.fir // get the actual FirRegularclass node
        val isRestricted = receiverClass.hasAnnotation(restrictedScopeAnnotation, session)

        if (!isRestricted) {
            return // The receiver class isn't restricted so the rules do not apply
        }

        val calleeSymbol =
            expression.toResolvedCallableSymbol() as? FirNamedFunctionSymbol ?: return

        // this check pretty much is ensuring we aren't blocking our Scope Builder function
        if (calleeSymbol.name.asString().contains("invoke", ignoreCase = true)) {
            // TODO, can this be more robust?
            // check if the receiver of teh 'invoke' is a parameter.
            // fun `func()`, the dispatchReceiver should be a reference to func
            val receiverSymbol = expression.dispatchReceiver?.toResolvedCallableSymbol(session)
            if (receiverSymbol is FirValueParameterSymbol) {
                // this is a call like func(), we should allow it.
                return
            }
        }

        // Only apply strict rules to calls that have are of type @Composable
        if (!calleeSymbol.hasAnnotation(composableAnnotation, session)) {
            return
        }

        // Validate the call - does it belong to the expected receiver?
        val isCallAllowed = isCallAllowed(expression, calleeSymbol, expectedReceiverType)

        if (!isCallAllowed) {
            reporter.reportOn(
                expression.source,
                DISALLOWED_CALL_IN_SCOPE,
                calleeSymbol.name.asString(),
                context
            )
        }

    }

    private fun isCallAllowed(
        call: FirFunctionCall,
        calleeSymbol: FirNamedFunctionSymbol,
        expectedReceiverType: ConeKotlinType
    ): Boolean {
        val expectedReceiverClassId = expectedReceiverType.classId ?: return false

        // First check, Is it called on an explicit receiver of the correct type?
        val dispatchReceiver = call.dispatchReceiver

        if (dispatchReceiver != null) {
            if (dispatchReceiver.resolvedType.classId == expectedReceiverClassId) {
                return true
            }
        }

        val extensionReceiver = call.extensionReceiver
        if (extensionReceiver != null) {
            if (extensionReceiver.resolvedType.classId == expectedReceiverClassId) {
                return true
            }
        }

        // Second Check, is it an implicit call where the symbol belongs to the expected type
        if (calleeSymbol.dispatchReceiverType?.classId == expectedReceiverClassId) {
            return true
        }

        if (calleeSymbol.containingClassLookupTag()?.classId == expectedReceiverClassId) {
            return true
        }

        return false
    }

}