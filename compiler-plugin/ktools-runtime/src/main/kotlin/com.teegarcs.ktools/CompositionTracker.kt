package com.teegarcs.ktools

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionTracker is provided via an optional CompositionLocal so that during test scenarios
 * compositions can be tracked and asserted against.
 */
class CompositionTracker {

    private val _trackedCompositions = mutableListOf<String>()
    val trackedCompositions: List<String> = _trackedCompositions

    /**
     * Track a composition by a provided name, typically the function name.
     */
    internal fun trackComposition(name: String) {
        _trackedCompositions.add(name) //TODO, expand to also track composable params

    }

    /**
     * clears tracked compositions. Useful if youa re about to perform a new set of assertions.
     * Call this before you perform an action that would trigger recompositions.
     */

    fun reset() {
        _trackedCompositions.clear()
    }

    /**
     * Assert a list of compositions in order. If the list is not in order or an item is not
     * found an exception will be thrown.
     */
    fun assertCompositions(vararg compositions: String) {
        val expectedList = compositions.toList()
        if (expectedList.isEmpty()) {
            return
        }

        if (trackedCompositions.size < expectedList.size) {
            throw (AssertionError("Expected compositions in order: $expectedList, but found $trackedCompositions."))
        }

        val actualSubList = trackedCompositions.take(expectedList.size)
        if (actualSubList != expectedList) {
            throw (AssertionError("Expected compositions in order: $expectedList, but found $trackedCompositions."))
        }
    }

    /**
     * Assert a composition by its name and count. If the composition is not found, an exception
     * will be thrown. If the composition is found more than the provided count an exception will
     * be thrown. Defaults to -1, which means the compositions can appear any number of times.
     */
    fun assertComposition(composition: String, exactly: Int = -1) {
        if (composition.isEmpty()) {
            return
        }

        val compositionCount = trackedCompositions.count { it == composition }

        if ((exactly > -1 && compositionCount != exactly) || (exactly == -1 && compositionCount == 0)) {
            throw (AssertionError("Expected composition $composition to be recomposed exactly $exactly but found $compositionCount times"))
        }
    }


    fun assertNotComposed(composition: String) {
        val compositionCount = trackedCompositions.count { it == composition }

        if (compositionCount > 0) {
            throw (AssertionError("Expected composition $composition to not be recomposed but found $compositionCount times"))
        }
    }
}

// default to null. Test or environment must provide tracker class for compositions to be tracked.
val LocalCompositionTracker = staticCompositionLocalOf<CompositionTracker?> { null }

@Composable
fun CompositionTracker(func: @Composable CompositionTracker.() -> Unit) =
    CompositionTracker().run {
        CompositionLocalProvider(LocalCompositionTracker provides this) {
            func()
        }
    }

/**
 * to be called by the compiler plugin
 */
@Composable
fun TrackComposition(name: String, shouldLog: Boolean, compositionTime: Long) {
    LocalCompositionTracker.current?.trackComposition(name)
    if (shouldLog) {
        Log.d("CompositionTracker", "Component: $name recomposed at $compositionTime")
    }
}
