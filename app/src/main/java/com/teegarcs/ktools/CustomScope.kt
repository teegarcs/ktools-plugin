package com.teegarcs.ktools

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

//@RestrictedScope
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
        // allowed
        VariantOne()
        VariantTwo()

        // not allowed
//        Text("test")
    }
}