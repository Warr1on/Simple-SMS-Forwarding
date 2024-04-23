package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simplemobiletools.smsmessenger.R
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdSectionHeader
import ru.warr1on.simplesmsforwarding.presentation.core.components.VerticalSpacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * A text field component designed to be used in a forwarding rule editor.
 *
 * Has a title header, and a text field with animated functionality like
 * a clear button and supporting text.
 *
 * @param title The title label of the text field. Will be displayed
 * in the form of a default section header.
 * @param text Current text value of the text field
 * @param onValueChange The callback that will be passed to the text field's onValueChange param
 * @param supportingText Supporting text of the text field
 * @param isError If true, signals error state of the text field.
 * Will change the text field's and title label's accent color to error color.
 * @param capitalization Capitalization for this text field
 * @param modifier An optional modifier that will be applied to the whole component's container
 */
@Composable
fun ForwardingRuleEditorTextField(
    title: String,
    text: String,
    onValueChange: (String) -> Unit,
    supportingText: String? = null,
    isError: Boolean = false,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val shouldShowClearButton by remember(text) {
        derivedStateOf { text.isNotEmpty() && isFocused }
    }

    // This is needed for preserving the supporting text during
    // its exit animation. Last supporting text will be either
    // the current supportingText, or the previous non-null and
    // non-empty one. This is ensured by setting lastSupportingText
    // when recalculating hasSupportText value if the conditions are met
    var lastSupportingText by remember {
        mutableStateOf(supportingText)
    }
    val hasSupportingText by remember(supportingText) {
        if (!supportingText.isNullOrBlank()) {
            lastSupportingText = supportingText
        }
        derivedStateOf { !supportingText.isNullOrBlank() }
    }

    val derivedTitleColor = when (isError) {
        false -> MaterialTheme.colorScheme.primary
        true -> MaterialTheme.colorScheme.error
    }
    val titleColor by animateColorAsState(
        targetValue = derivedTitleColor,
        label = "title_color_anim"
    )

    Column(modifier = modifier) {

        FwdSectionHeader(
            title = title,
            color = titleColor
        )

        VerticalSpacer(height = 12.dp)

        TextField(
            value = text,
            onValueChange = onValueChange,
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                capitalization = capitalization,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = shouldShowClearButton,
                    enter = fadeIn() + scaleIn(spring()),
                    exit = fadeOut() + scaleOut(spring())
                ) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel_circled),
                            contentDescription = "Clear input"
                        )
                    }
                }
            },
            supportingText = {
                AnimatedVisibility(
                    visible = hasSupportingText,
                    label = "supporting_text_visibility_anim"
                ) {
                    AnimatedContent(
                        targetState = lastSupportingText,
                        label = "supporting_text_content_anim"
                    ) {
                        Text(text = it ?: "")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent { isFocused = it.isFocused }
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
            var text by remember { mutableStateOf("Value") }

            ForwardingRuleEditorTextField(
                title = "Title",
                text = text,
                onValueChange = { text = it }
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
            var text by remember { mutableStateOf("Value") }

            ForwardingRuleEditorTextField(
                title = "Title",
                text = text,
                onValueChange = { text = it },
                isError = true
            )
        }
    }
}
