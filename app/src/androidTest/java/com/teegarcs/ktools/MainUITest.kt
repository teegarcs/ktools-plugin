package com.teegarcs.ktools

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MainUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun exampleCompositionTest() {
        val vm = MainViewModel()

        lateinit var compositionTracker: CompositionTracker
        composeTestRule.setContent {
            CompositionTracker {
                compositionTracker = this
                val state = vm.viewState.collectAsState().value
                MainUI.BuildUI(state, vm)
            }
        }

        // assert the initial compositions
        compositionTracker.assertCompositions(
            "BuildUI", "Column", "Button", "Button"
        )
        // reset the tracker to clear initial compositions
        compositionTracker.reset()

        //user action
        composeTestRule.onNodeWithText("Increment").performClick()
        composeTestRule.onNodeWithText("Count: 1").assertExists()

        // assert that only the composables we expect to be recomposed after a user action are recomposed
        compositionTracker.assertCompositions("BuildUI", "Column")
        //assert the composition was not triggered
        compositionTracker.assertNotComposed("Button")
    }
}