package com.teegarcs.ktools.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * BaseViewModel that manages State, Actions, and SideEffects for a given feature.
 */
abstract class BaseViewModel<State : Any, Action : Any, SideEffect : Any> : ViewModel(),
    Interactor<Action, SideEffect> {

    private val _viewState: MutableStateFlow<State> by lazy {
        MutableStateFlow(buildInitialState())
    }

    val viewState: StateFlow<State>
        get() = _viewState.asStateFlow()

    val currentState: State
        get() = viewState.value

    private val _sideEffectsChannel = Channel<SideEffect>(Channel.UNLIMITED)
    private val sideEffects: Flow<SideEffect>
        get() = _sideEffectsChannel.receiveAsFlow()

    /**
     * function required to implement and return the initial state of the feature
     */
    abstract fun buildInitialState(): State

    /**
     * Function to post a SideEffect to the SideEffect Channel
     */
    protected fun sendSideEffect(sideEffect: SideEffect) {
        _sideEffectsChannel.trySend(sideEffect)
    }

    @Composable
    override fun BindSideEffect(callback: (SideEffect) -> Unit) {
        LaunchedEffect(key1 = Unit) {
            sideEffects.collect {
                callback(it)
            }
        }
    }

    protected fun updateState(updated: State): Boolean {
        return if (_viewState.value != updated) {
            _viewState.value = updated
            true
        } else {
            false
        }
    }

    protected fun updateState(updateFunction: State.() -> State): Boolean =
        updateState(_viewState.value.updateFunction())

}