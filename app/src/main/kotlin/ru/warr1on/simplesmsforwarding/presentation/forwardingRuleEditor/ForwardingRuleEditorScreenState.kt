package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor

import androidx.compose.runtime.Immutable
import ru.warr1on.simplesmsforwarding.presentation.shared.PresentationModel

/**
 * Forwarding rule editor screen state
 *
 * @param screenTitle Displayed title of the screen
 * @param ruleNameTextFieldState State of the "Rule name" text field
 * @param messageTypeTextFieldState State of the "Message type key" text field
 * @param addressesBlockState State of the phone addresses UI block
 * @param addNewAddressDialogState State of the "add new phone address" dialog
 */
@Immutable
data class ForwardingRuleEditorScreenState(
    val screenTitle: String,
    val ruleNameTextFieldState: TextFieldState,
    val messageTypeTextFieldState: TextFieldState,
    val addressesBlockState: AddressesBlockState,
    val filtersBlockState: FiltersBlockState,
    val addNewAddressDialogState: AddNewAddressDialogState
) {

    /**
     * State of the forwarding rule editor's text field
     *
     * @param text Current text inside the text field
     * @param isError Signals error state if true
     * @param supportingText This text will be displayed below the text field
     */
    @Immutable
    data class TextFieldState(
        val text: String,
        val isError: Boolean,
        val supportingText: String?
    )

    /**
     * State of the phone addresses UI block
     *
     * @param addresses Phone addresses currently added to the rule
     */
    @Immutable
    data class AddressesBlockState(
        val addresses: List<String>
    )

    /**
     * State of the text filters UI block
     *
     * @param filters Filters currently added to the rule
     */
    @Immutable
    data class FiltersBlockState(
        val filters: List<PresentationModel.ForwardingFilter>
    )

    /**
     * State of the "add new phone address" dialog
     *
     * @see Showing
     * @see NotShowing
     */
    sealed interface AddNewAddressDialogState {

        /**
         * "Add new phone address" dialog is currently not shown
         */
        data object NotShowing : AddNewAddressDialogState

        /**
         * "Add new phone address" dialog is currently shown
         *
         * @param textFieldState State of the phone address input text field
         * @param canAddCurrentInputAsAddress Signals whether the current text
         * field input can be added to the list of addresses for this rule
         */
        data class Showing(
            val textFieldState: TextFieldState,
            val canAddCurrentInputAsAddress: Boolean
        ) : AddNewAddressDialogState
    }
}
