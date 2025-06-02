package com.teegarcs.ktools

import com.teegarcs.ktools.core.BaseViewModel

class MainViewModel : BaseViewModel<MainState, MainAction, MainSE>() {

    override fun buildInitialState() = MainState()

    override fun sendAction(action: MainAction) {
        when (action) {
            MainAction.Decrement -> updateState { copy(count = count - 1) }
            MainAction.Increment -> updateState { copy(count = count + 1) }
        }
    }
}

data class MainState(
    val label: String = "Tap to Increment and Decrement",
    val count: Int = 0
)

sealed class MainAction() {
    data object Increment : MainAction()
    data object Decrement : MainAction()
}

sealed class MainSE()