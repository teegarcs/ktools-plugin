package com.teegarcs.ktools.core

import androidx.compose.runtime.Composable

/**
 * Provides a mechanism for the compose functions within the ComposeUI to interact with the ViewModel
 * to do things such as send [Action] or bind to [SideEffect].
 */
interface Interactor <Action : Any, SideEffect : Any> {

    /**
     * Binds a lambda to the [SideEffect] emitted by the ViewModel. This lambda will be called when
     * a [SideEffect] is received by the BaseViewModel's SideEffect Flow.
     */
    @Composable
    fun BindSideEffect(callback: (SideEffect) -> Unit) {}

    /**
     * Sends an [Action] to the ViewModel
     *
     * @param action the action to send to the ViewModel
     */
    fun sendAction(action: Action){}
}