package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.components.*
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalPopup
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenActions
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenState
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ForwardingRuleEditorScreenState.FilterEditorDialogState
import ru.warr1on.simplesmsforwarding.presentation.shared.PresentationModel.ForwardingFilter

@Composable
fun ForwardingRuleEditorAddFilterDialog(
    state: FilterEditorDialogState,
    actions: ForwardingRuleEditorScreenActions.FilterEditorDialogActions,
    modifier: Modifier = Modifier
) {
    if (state is FilterEditorDialogState.Displayed) {
        ModalPopup(
            onDismissed = { _, _ ->
                actions.onDialogDismissed()
            },
            applyImePadding = true
        ) {
            AddFilterDialogContent(
                state = state,
                actions = actions,
                modifier = modifier,
                onDismissRequest = { this.dismiss() }
            )
        }
    }
}

@Composable
private fun AddFilterDialogContent(
    state: FilterEditorDialogState.Displayed,
    actions: ForwardingRuleEditorScreenActions.FilterEditorDialogActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    MaterialYouStyleBasicDialogScaffold(
        title = "New filter",
        actions = {
            action("Cancel") { onDismissRequest() }
            action("Add") { actions.onSaveFilterRequest() }
        },
        modifier = modifier
    ) {
        Column {

            SegmentedButton(
                selection = state.filterTypeSelection,
                onSegmentSelectionChange = actions.onFilterTypeSelectionChangeRequest,
                modifier = Modifier.fillMaxWidth(),
            ) {
                segmentWithText("Include", ForwardingFilter.FilterType.INCLUDE)
                segmentWithText("Exclude", ForwardingFilter.FilterType.EXCLUDE)
            }

            Spacer(24.dp)

            FwdTextField(
                text = state.textFieldState.text,
                onValueChange = actions.onTextInputRequest,
                labelText = "Filtered text",
                supportingText = state.textFieldState.supportingText,
                singleLine = false,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FilterTypeSelector() {

}


//region Previews

@Preview
@Composable
private fun ForwardingRuleEditorAddFilterDialog_Preview() {

    val state = remember {
        FilterEditorDialogState.Displayed(
            textFieldState = ForwardingRuleEditorScreenState.TextFieldState(
                text = "Some text that should pass this filter",
                isError = false,
                supportingText = "A message would pass this filter when the specified text (respecting case) is present in it"
            ),
            filterTypeSelection = ForwardingFilter.FilterType.INCLUDE,
            ignoresCaseToggleState = false,
            canAddCurrentInputAsFilter = true
        )
    }

    val actions = ForwardingRuleEditorScreenActions.FilterEditorDialogActions(
        onFilterTypeSelectionChangeRequest = {},
        onIgnoresCaseSelectionChangeRequest = {},
        onTextInputRequest = {},
        onSaveFilterRequest = {},
        onDialogDismissed = {}
    )

    AppTheme {
        PreviewBox(backgroundColor = Color.Black) {
            AddFilterDialogContent(
                state = state,
                actions = actions,
                onDismissRequest = {},
                modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)
            )
        }
    }
}

//endregion
