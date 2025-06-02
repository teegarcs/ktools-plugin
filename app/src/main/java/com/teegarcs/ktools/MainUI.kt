package com.teegarcs.ktools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.teegarcs.ktools.core.ComposeUI
import com.teegarcs.ktools.core.Interactor

object MainUI : ComposeUI<MainState, MainAction, MainSE>() {
    @Composable
    override fun BuildUI(
        state: MainState, interactor: Interactor<MainAction, MainSE>
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = state.label)
            Text(text = "Count: ${state.count}", Modifier.padding(top = 16.dp, bottom = 16.dp))

            Button(
                onClick = { interactor.sendAction(MainAction.Increment) }
            ) {
                Text(text = "Increment")
            }

            Button(
                onClick = { interactor.sendAction(MainAction.Decrement) }
            ) {
                Text(text = "Decrement")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    MainUI.BuildUI(MainState(), object : Interactor<MainAction, MainSE> {})
}