package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simplemobiletools.smsmessenger.R
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultTextButton
import ru.warr1on.simplesmsforwarding.presentation.core.components.Spacer
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalHostExperimental
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalPopup
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun ForwardingRuleEditorAddPhoneAddressDialog(
    isShown: MutableState<Boolean>,
) {
    if (isShown.value) {
        ModalPopup(
            onDismissed = { dismissedInternally, stateSyncRequired ->
                isShown.value = false
            },
            applyImePadding = true
        ) {
            AddPhoneAddressDialogContent(
                onDismissRequest = { this.dismiss() }
            )
        }
    }
}

@Composable
private fun AddPhoneAddressDialogContent(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {
    var textFieldText by remember { mutableStateOf("") }
    val shouldDisableAddButton by remember { derivedStateOf { textFieldText.isBlank() } }
    val shouldShowTextClearButton by remember { derivedStateOf { textFieldText.isNotEmpty() }}

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

        AddPhoneAddressDialogTextField(
            text = textFieldText,
            onValueChange = { textFieldText = it },
            onClearInputRequest = { textFieldText = "" },
            shouldShowClearButton = shouldShowTextClearButton
        )

        AddPhoneAddressDialogActionButtons(
            shouldDisableAddButton = shouldDisableAddButton,
            onCancelClicked = onDismissRequest,
            onAddClicked = {},
            modifier = Modifier
                .align(Alignment.End)
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun AddPhoneAddressDialogTextField(
    text: String,
    onValueChange: (String) -> Unit,
    onClearInputRequest: () -> Unit,
    shouldShowClearButton: Boolean
) {
    TextField(
        value = text,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = {
            Text(
                text = "New number",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = null)},
        trailingIcon = {
            AnimatedVisibility(
                visible = shouldShowClearButton,
                enter = fadeIn() + scaleIn(spring()),
                exit = fadeOut() + scaleOut(spring())
            ) {
                IconButton(
                    onClick = onClearInputRequest
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel_circled),
                        contentDescription = "Clear input"
                    )
                }
            }
        }
    )
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
    AppTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(16.dp)
        ) {
            AddPhoneAddressDialogContent(
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

    var shouldDisplayAlert = remember { mutableStateOf(false) }

    AppTheme {
        ModalHostExperimental {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {
                Button(onClick = { shouldDisplayAlert.value = true }) {
                    Text("Click me", color = MaterialTheme.colorScheme.onPrimary)
                }

                ForwardingRuleEditorAddPhoneAddressDialog(
                    isShown = shouldDisplayAlert
                )
            }
        }
    }
}
