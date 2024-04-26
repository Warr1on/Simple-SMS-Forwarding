package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.model

import androidx.compose.runtime.Immutable
import ru.warr1on.simplesmsforwarding.presentation.shared.PresentationModel

/**
 * Forwarding rule editor screen state
 *
 * @param screenTitle Displayed title of the screen
 * @param ruleNameTextFieldState State of the "Rule name" text field
 * @param messageTypeTextFieldState State of the "Message type key" text field
 * @param addressesBlockState State of the phone addresses UI block
 */
@Immutable
data class ForwardingRuleEditorScreenState(
    val screenTitle: String,
    val ruleNameTextFieldState: TextFieldState,
    val messageTypeTextFieldState: TextFieldState,
    val addressesBlockState: AddressesBlockState
) {

    /**
     * State of the forwarding rule editor's text field
     *
     * @param text Current text inside the text field
     * @param isError Signals error state if true
     * @param supportingText This text will be displayed below the text field
     * @param onTextChange Called whenever the text inside the text field would want
     * to change. Will take up the new proposed text as a parameter
     */
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

    @Immutable
    data class FiltersBlockState(
        val filters: List<PresentationModel.ForwardingFilter>,
        val onAddNewFilterRequest: () -> Unit
    )

    @Immutable
    data class AddNewAddressDialogState(
        val textFieldState: TextFieldState,
        val canAddCurrentInputAsAddress: Boolean,
        val onAddAddressRequest: (phoneAddress: String) -> Unit
    )
}
