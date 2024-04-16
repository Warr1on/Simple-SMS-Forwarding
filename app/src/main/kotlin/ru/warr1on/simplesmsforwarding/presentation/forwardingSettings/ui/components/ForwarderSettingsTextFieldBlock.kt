package ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * Forwarder setting view block with a text field.
 *
 * Has a header with the setting's name, a text field input,
 * and the buttons to confirm or discard the input, that are shown
 * when the input text changes and is different from the current value.
 *
 * @param      settingName The name of the setting that this UI block represents
 * @param     currentValue Current value of the setting
 * @param onCommitNewValue Is called when the user wants to update the setting's value
 */
@Composable
fun ForwarderSettingTextFieldBlock(
    settingName: String,
    currentValue: String,
    onCommitNewValue: (newValue: String) -> Unit
) {
    var text by remember(currentValue) { mutableStateOf(currentValue) }
    var shouldDisplaySavingInputs by remember(currentValue) { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(Modifier.fillMaxWidth()) {

        ForwarderSettingHeader(title = settingName)

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = text,
            onValueChange = {
                text = it
                shouldDisplaySavingInputs = (text != currentValue)
            },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                if (text != currentValue) {
                    focusManager.clearFocus()
                    onCommitNewValue(text)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = shouldDisplaySavingInputs) {
            Row {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.1f))
                ForwarderSettingTextFieldSavingInputs(
                    onCommitNewValue = {
                        focusManager.clearFocus()
                        onCommitNewValue(text)
                    },
                    onDeclineNewValue = {
                        focusManager.clearFocus()
                        text = currentValue
                        shouldDisplaySavingInputs = false
                    }
                )
            }
        }
    }
}

/**
 * Two buttons that are shown below the text view whenever the
 * input text is different from the setting's current value.
 *
 * They allow user to save or discard value changes.
 *
 * @param  onCommitNewValue Is called when the user confirms that they want to save the new value
 * @param onDeclineNewValue Is called when the user want to discard changes and revert to the
 *                          currently saved value
 */
@Composable
private fun ForwarderSettingTextFieldSavingInputs(
    onCommitNewValue: () -> Unit,
    onDeclineNewValue: () -> Unit
) {
    Row {
        SavingInputSimpleTextButton(
            text = "Cancel",
            color = MaterialTheme.colorScheme.error,
            onClick = onDeclineNewValue
        )

        Spacer(Modifier.width(8.dp))

        SavingInputSimpleTextButton(
            text = "Save",
            color = MaterialTheme.colorScheme.primary,
            onClick = onCommitNewValue
        )
    }
}

@Composable
private fun SavingInputSimpleTextButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    val ripple = rememberRipple()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        Modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple,
                onClick = onClick
            )
//            .background(Color.LightGray)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = color
        )
    }
}


//--- Previews ---//

@Preview
@Composable
private fun ForwardingBackendEndpointSettingBlock_Preview() {
    AppTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
        ) {
            ForwarderSettingTextFieldBlock(
                settingName = "Backend Endpoint",
                currentValue = "https://test.com/fwdbot",
                onCommitNewValue = { newValue -> }
            )
        }
    }
}

@Preview
@Composable
private fun ForwarderSettingTextFieldSavingInputs_Preview() {
    AppTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .border(width = 1.dp, color = Color.Gray)
        ) {
            ForwarderSettingTextFieldSavingInputs(
                onCommitNewValue = {},
                onDeclineNewValue = {}
            )
        }
    }
}
