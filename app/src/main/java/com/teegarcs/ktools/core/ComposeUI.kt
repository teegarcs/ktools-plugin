package com.teegarcs.ktools.core

import androidx.compose.runtime.Composable

/**
 * ComposeUI is a class that is responsible for building the UI for a given feature. It Provides
 *  a BuildUI function that takes the current state, a function to send actions, and a function to
 *  bind side effects. The ComposeUI creates a simplified way for your Compose enabled features to
 *  interact with your ViewModel.
 */
abstract class ComposeUI <in State: Any, Action : Any, SideEffect :Any>{

    /**
     * Takes the current [State]that will drive the Build UI Composer function, a function to send
     * [Actions]s to the VM, and a function to observe [SideEffect]s emitted by the VM that can be
     * used in a compose friendly way
     *
     * @param state the current state of the feature
     * @param interactor a class that provides the ability for the ComposeUI to interact with the VM
     */
    @Composable
    abstract fun BuildUI(state: State, interactor: Interactor<Action, SideEffect>)
}