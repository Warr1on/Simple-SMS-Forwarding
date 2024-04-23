package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.model

import androidx.compose.runtime.Immutable

@Immutable
data class ForwardingRuleEditorScreenState(
    val screenTitle: String,
    val ruleNameTextFieldState: TextFieldState,
    val messageTypeTextFieldState: TextFieldState,
    val addressesBlockState: AddressesBlockState
) {

    @Immutable
    data class TextFieldState(
        val text: String,
        val isError: Boolean,
        val supportingText: String?,
        val onTextChange: (String) -> Unit
    )

    @Immutable
    data class AddressesBlockState(
        val addresses: List<String>,
        val modalState: ModalState,
        val onAddAddressRequest: (phoneAddress: String) -> Unit,
        val onRemoveAddressRequest: (phoneAddress: String) -> Unit
    ) {
        enum class ModalState {
            NONE, ADD_ADDRESS_SHOWN, ADD_FROM_KNOWN_SHOWN
        }
    }
}
