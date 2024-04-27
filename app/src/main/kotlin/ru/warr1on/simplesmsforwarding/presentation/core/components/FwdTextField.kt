package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simplemobiletools.smsmessenger.R
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * A general-purpose text field component that packs a little bit of additional commonly useful
 * functionality. Has a built-in clear button, animates support text change, and automatically
 * resigns focus on keyboard action (which can be overridden by a [resignFocusOnKeyboardAction] param).
 *
 * @param text Current text value of the text field
 * @param onValueChange The callback that is triggered when the input service updates the text.
 * An updated text comes as a parameter of the callback
 * @param enabled Controls the enabled state of this text field. When `false`, this component will
 * not respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param readOnly Controls the editable state of the text field. When `true`, the text field cannot
 * be modified. However, a user can focus it and copy text from it. Read-only text fields are
 * usually used to display pre-filled forms that a user cannot edit.
 * @param labelText Text of the text field's label. Null by default, which means that the text
 * field won't have a label
 * @param placeholderText Text of the text field's placeholder. Null by default, which means that
 * the text field won't have a placeholder
 * @param supportingText Supporting text that will be placed below the text field
 * @param leadingIcon Text field's leading icon
 * @param isError If true, signals error state of the text field.
 * Will change the text field's and title label's accent color to error color.
 * @param keyboardType The type of the keyboard for this text field
 * @param keyboardImeAction The IME action that the keyboard for this text field should have.
 * @param keyboardActions Keyboard actions for this text field. See [KeyboardActions]
 * @param resignFocusOnKeyboardAction Determines if the text field should resign focus on keyboard
 * action performed. `True` by default.
 * @param capitalization Capitalization for this text field
 * @param autoCorrect Determines whether the keyboard should have auto correct enabled for this
 * text field
 * @param singleLine When `true`, this text field becomes a single horizontally scrolling text field
 * instead of wrapping onto multiple lines. The keyboard will be informed to not show the return key
 * as the [ImeAction]. Note that [maxLines] parameter will be ignored as the maxLines attribute will
 * be automatically set to 1.
 * @param minLines The minimum height in terms of minimum number of visible lines. It is required
 * that 1 <= [minLines] <= [maxLines]. This parameter is ignored when [singleLine] is true.
 * @param maxLines The maximum height in terms of maximum number of visible lines. It is required
 * that 1 <= [minLines] <= [maxLines]. This parameter is ignored when [singleLine] is true.
 * @param modifier An optional modifier that will be applied to the whole component's container
 */
@Composable
fun FwdTextField(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    labelText: String? = null,
    placeholderText: String? = null,
    supportingText: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardImeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions? = null,
    resignFocusOnKeyboardAction: Boolean = true,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    autoCorrect: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    val resignFocusIfNecessary = remember(resignFocusOnKeyboardAction) {
        return@remember {
            if (resignFocusOnKeyboardAction) {
                focusManager.clearFocus()
            }
        }
    }

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

    val label: (@Composable () -> Unit)? = remember(labelText) {
        when (labelText) {
            null -> null
            else -> {
                @Composable { Text(labelText) }
            }
        }
    }

    val placeholder: (@Composable () -> Unit)? = remember(placeholderText) {
        when (placeholderText) {
            null -> null
            else -> {
                @Composable { Text(placeholderText) }
            }
        }
    }

    TextField(
        value = text,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            autoCorrect = autoCorrect,
            capitalization = capitalization,
            imeAction = keyboardImeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardActions?.onDone?.let { it() }
                resignFocusIfNecessary()
            },
            onGo = {
                keyboardActions?.onGo?.let { it() }
                resignFocusIfNecessary()
            },
            onNext = {
                keyboardActions?.onNext?.let { it() }
                resignFocusIfNecessary()
            },
            onPrevious = {
                keyboardActions?.onPrevious?.let { it() }
                resignFocusIfNecessary()
            },
            onSearch = {
                keyboardActions?.onSearch?.let { it() }
                resignFocusIfNecessary()
            },
            onSend = {
                keyboardActions?.onSend?.let { it() }
                resignFocusIfNecessary()
            }
        ),
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
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
                label = "textfield_supporting_text_visibility_anim"
            ) {
                AnimatedContent(
                    targetState = lastSupportingText,
                    label = "textfield_supporting_text_content_anim"
                ) {
                    Text(text = it ?: "")
                }
            }
        },
        modifier = modifier
            .onFocusEvent { isFocused = it.isFocused }
    )
}


//--- Previews ---//

@Preview
@Composable
private fun FwdTextField_Preview() {
    AppTheme {
        Box(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            var text by remember { mutableStateOf("Value") }

            FwdTextField(
                text = text,
                onValueChange = { text = it },
                labelText = "Label",
                placeholderText = "Placeholder"
            )
        }
    }
}

@Preview
@Composable
private fun FwdTextField_ErrorStatePreview() {
    AppTheme {
        Box(
            Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            var text by remember { mutableStateOf("Value") }

            FwdTextField(
                text = text,
                onValueChange = { text = it },
                isError = true
            )
        }
    }
}
