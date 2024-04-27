package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor

import androidx.compose.runtime.Immutable

/**
 * A composite data class that contains all of the possible
 * actions that a forwarding rule editor screen can trigger
 *
 * @param ruleNameTextFieldActions Actions for the rule name input text field
 * @param messageTypeKeyTextFieldActions Actions for the message type key input text field
 * @param addressesComponentActions Actions for the addresses component
 * @param filtersComponentActions Actions for the filters component
 * @param addNewAddressesDialogActions Actions for the "add new phone address" dialog
 */
@Immutable
data class ForwardingRuleEditorScreenActions(
    val ruleNameTextFieldActions: TextFieldActions,
    val messageTypeKeyTextFieldActions: TextFieldActions,
    val addressesComponentActions: AddressesComponentActions,
    val filtersComponentActions: FiltersComponentActions,
    val addNewAddressesDialogActions: AddNewAddressDialogActions

) {
    /**
     * Actions for the text field in a forwarding rule editor
     *
     * @param onTextInputRequest Called whenever the text inside the text field would want
     * to change. Will take up the new proposed text as a parameter
     */
    @Immutable
    data class TextFieldActions(
        val onTextInputRequest: (proposedNewText: String) -> Unit
    )

    /**
     * Actions for the phone addresses component
     *
     * @param onAddNewAddressRequest Called when the user wants to add a new phone address.
     * Should open the "add new phone address" dialog
     * @param onAddFromKnownRequest Called when the user wants to add a phone address from
     * the chat history or the contacts. Should open the "add from known" dialog
     * @param onRemoveAddressRequest Called when the user wants to remove a specific phone
     * address from the list of addresses added to the rule. Takes the phone address string
     * as a parameter.
     */
    @Immutable
    data class AddressesComponentActions(
        val onAddNewAddressRequest: () -> Unit,
        val onAddFromKnownRequest: () -> Unit,
        val onRemoveAddressRequest: (phoneAddress: String) -> Unit
    )

    /**
     * Actions for the filters component
     *
     * @param onAddNewFilter Called when the user wants to add a new filter to the rule
     * @param onRemoveFilter Called when the user wants to remove a specific filter from
     * the rule. Takes the filter's ID as a parameter.
     */
    @Immutable
    data class FiltersComponentActions(
        val onAddNewFilter: () -> Unit,
        val onRemoveFilter: (filterID: String) -> Unit
    )

    /**
     * Actions for the "add new phone address" dialog
     *
     * @param onTextInputRequest Called whenever the text inside the phone address input
     * text field would want to change. Takes the proposed new text as a parameter.
     * @param onAddNewAddressRequest Called when the user wants to add the specified phone
     * address to the rule
     * @param onDialogDismissed Called when the dialog was dismissed. This call should be
     * made AFTER the fact, and it should sync the dialog state according to that fact
     */
    @Immutable
    data class AddNewAddressDialogActions(
        val onTextInputRequest: (proposedNewText: String) -> Unit,
        val onAddNewAddressRequest: (phoneAddress: String) -> Unit,
        val onDialogDismissed: () -> Unit
    )
}
