package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultTextButton
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdTextField
import ru.warr1on.simplesmsforwarding.presentation.core.components.Spacer
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalHostExperimental
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalPopup
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenActions.AddNewAddressDialogActions
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenState
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenState.AddNewAddressDialogState

@Composable
fun ForwardingRuleEditorAddPhoneAddressDialog(
    state: AddNewAddressDialogState,
    actions: AddNewAddressDialogActions
) {
    if (state is AddNewAddressDialogState.Showing) {
        ModalPopup(
            onDismissed = { _, _ -> actions.onDialogDismissed() },
            applyImePadding = true
        ) {
            AddPhoneAddressDialogContent(
                state = state,
                actions = actions,
                onDismissRequest = { this.dismiss() }
            )
        }
    }
}

@Composable
private fun AddPhoneAddressDialogContent(
    state: AddNewAddressDialogState.Showing,
    actions: AddNewAddressDialogActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                top = 24.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 8.dp
            )
    ) {
        Text(
            text = "Add number",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(16.dp)

        FwdTextField(
            text = state.textFieldState.text,
            onValueChange = { actions.onTextInputRequest(it) },
            placeholderText = "New number",
            supportingText = state.textFieldState.supportingText,
            isError = state.textFieldState.isError,
            leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = null) },
            singleLine = true,
            capitalization = KeyboardCapitalization.None
        )

        AddPhoneAddressDialogActionButtons(
            shouldDisableAddButton = !state.canAddCurrentInputAsAddress,
            onCancelClicked = onDismissRequest,
            onAddClicked = {
                actions.onAddNewAddressRequest()
                onDismissRequest()
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun AddPhoneAddressDialogActionButtons(
    modifier: Modifier = Modifier,
    shouldDisableAddButton: Boolean,
    onCancelClicked: () -> Unit,
    onAddClicked: () -> Unit
) {
    Row(modifier = modifier) {

        FwdDefaultTextButton(
            text = "Cancel",
            color = MaterialTheme.colorScheme.error,
            onClick = onCancelClicked
        )

        Spacer(4.dp)

        FwdDefaultTextButton(
            text = "Add",
            enabled = !shouldDisableAddButton,
            color = MaterialTheme.colorScheme.primary,
            onClick = onAddClicked
        )
    }
}


//--- Previews ---//

@Preview
@Composable
private fun AddPhoneAddressDialogContent_Preview() {

    val state = remember { generatePreviewShownDialogState() }
    val actions = remember {
        AddNewAddressDialogActions(
            onTextInputRequest = {},
            onAddNewAddressRequest = {},
            onDialogDismissed = {}
        )
    }

    AppTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(16.dp)
        ) {
            AddPhoneAddressDialogContent(
                state = state,
                actions = actions,
                modifier = Modifier.clip(MaterialTheme.shapes.extraLarge),
                onDismissRequest = {}
            )
        }
    }
}

// This preview is designed for interactive mode
@Preview
@Composable
private fun ForwardingRuleEditorAddPhoneAddressDialog_Preview() {

    var state: AddNewAddressDialogState by remember {
        mutableStateOf(AddNewAddressDialogState.NotShowing)
    }

    val actions = remember {
        AddNewAddressDialogActions(
            onTextInputRequest = {
                state = AddNewAddressDialogState.Showing(
                    textFieldState = ForwardingRuleEditorScreenState.TextFieldState(
                        text = it,
                        isError = false,
                        supportingText = null
                    ),
                    canAddCurrentInputAsAddress = it.isNotBlank(),
                )
            },
            onAddNewAddressRequest = {},
            onDialogDismissed = {
                state = AddNewAddressDialogState.NotShowing
            }
        )
    }

    AppTheme {
        ModalHostExperimental {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {
                Button(onClick = { state = generatePreviewShownDialogState() }) {
                    Text("Click me", color = MaterialTheme.colorScheme.onPrimary)
                }

                ForwardingRuleEditorAddPhoneAddressDialog(
                    state = state,
                    actions = actions
                )
            }
        }
    }
}

private fun generatePreviewShownDialogState(): AddNewAddressDialogState.Showing {
    return AddNewAddressDialogState.Showing(
        textFieldState = ForwardingRuleEditorScreenState.TextFieldState(
            text = "",
            isError = false,
            supportingText = null
        ),
        canAddCurrentInputAsAddress = false
    )
}
