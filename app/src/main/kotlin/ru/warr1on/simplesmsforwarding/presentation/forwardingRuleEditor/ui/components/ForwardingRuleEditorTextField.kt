package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdSectionHeader
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdTextField
import ru.warr1on.simplesmsforwarding.presentation.core.components.VerticalSpacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenActions
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenState

/**
 * A text field component designed to be used in a forwarding rule editor.
 * Designed to look like its own section in a list.
 *
 * Contains a section header, and a text field with animated functionality
 * like a clear button and supporting text.
 *
 * @param headerText The text that will be displayed in this text field's section header
 * @param state State of the text field, represented by [ForwardingRuleEditorScreenState.TextFieldState]
 * @param actions Actions that this text field can trigger. Represented by
 * [ForwardingRuleEditorScreenActions.TextFieldActions]
 * @param modifier Modifier for this UI component. Applied at the container level
 * @param capitalization Capitalization of the keyboard input for this text field
 */
@Composable
fun ForwardingRuleEditorTextField(
    headerText: String,
    state: ForwardingRuleEditorScreenState.TextFieldState,
    actions: ForwardingRuleEditorScreenActions.TextFieldActions,
    modifier: Modifier = Modifier,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
) {
    val derivedTitleColor = when (state.isError) {
        false -> MaterialTheme.colorScheme.primary
        true -> MaterialTheme.colorScheme.error
    }
    val titleColor by animateColorAsState(
        targetValue = derivedTitleColor,
        label = "title_color_anim"
    )

    Column(modifier = modifier) {

        FwdSectionHeader(
            title = headerText,
            color = titleColor
        )

        VerticalSpacer(height = 12.dp)

        FwdTextField(
            text = state.text,
            onValueChange = actions.onTextInputRequest,
            supportingText = state.supportingText,
            isError = state.isError,
            capitalization = capitalization,
            autoCorrect = false,
            keyboardImeAction = ImeAction.Done,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


//--- Previews ---//

@Preview
@Composable
private fun ForwardingRuleEditorTextField_Preview() {
    AppTheme {
        Box(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            ForwardingRuleEditorTextField(
                headerText = "Header",
                state = ForwardingRuleEditorScreenState.TextFieldState(
                    text = "Value",
                    isError = false,
                    supportingText = null
                ),
                actions = ForwardingRuleEditorScreenActions.TextFieldActions(
                    onTextInputRequest = {}
                )
            )
        }
    }
}

@Preview
@Composable
private fun ForwardingRuleEditorTextField_ErrorStatePreview() {
    AppTheme {
        Box(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            ForwardingRuleEditorTextField(
                headerText = "Header",
                state = ForwardingRuleEditorScreenState.TextFieldState(
                    text = "Value",
                    isError = true,
                    supportingText = "Input is invalid"
                ),
                actions = ForwardingRuleEditorScreenActions.TextFieldActions(
                    onTextInputRequest = {}
                )
            )
        }
    }
}
