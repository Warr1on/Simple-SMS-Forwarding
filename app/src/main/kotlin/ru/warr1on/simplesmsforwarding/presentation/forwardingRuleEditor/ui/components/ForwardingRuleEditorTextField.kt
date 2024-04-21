package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdSectionHeader
import ru.warr1on.simplesmsforwarding.presentation.core.components.VerticalSpacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun ForwardingRuleEditorTextField(
    title: String,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        FwdSectionHeader(title = title)

        VerticalSpacer(height = 12.dp)

        TextField(
            value = "Test",
            onValueChange = {

            },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                capitalization = capitalization,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {

            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ForwardingRuleEditorTextField_Preview() {
    AppTheme {
        Box(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            ForwardingRuleEditorTextField(title = "Title")
        }
    }
}
