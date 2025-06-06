package com.teegarcs.ktools

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@RestrictedScope
object CustomScope {

    @Composable
    fun VariantOne() { /* Implementation */
    }

    @Composable
    fun VariantTwo() { /* Implementation */
    }
}

@Composable
fun ScopeComponentGroup(
    func: @Composable CustomScope.() -> Unit
) = CustomScope.func()

@Composable
private fun ComponentTest() {
    ScopeComponentGroup {
        // allowed Composables
        VariantOne()
        VariantTwo()

        //allowed any other non-composable
        Log.d("tag", "something")

        // not allowed Composables, show error
//        Text("test")
//        Button() { }
    }
}