package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultPresetCard
import ru.warr1on.simplesmsforwarding.presentation.core.components.Spacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun ForwardingRuleEditorPhoneAddressEntry(
    phoneAddress: String,
    onRemovePhoneAddress: (address: String) -> Unit,
    modifier: Modifier = Modifier
) {
    FwdDefaultPresetCard(
        elevation = 1.dp,
        disableShadow = true,
        useOutlineForDarkTheme = false,
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Spacer(16.dp)

                Text(
                    text = phoneAddress,
                    fontSize = 18.sp
                )

                Spacer(16.dp)
            }

            Spacer()

            IconButton(onClick = { onRemovePhoneAddress(phoneAddress) }) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Remove number",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


//--- Previews ---//

@Preview
@Composable
private fun ForwardingRuleEditorPhoneAddressEntry_Preview() {
    AppTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            ForwardingRuleEditorPhoneAddressEntry(
                phoneAddress = "+79425174237",
                onRemovePhoneAddress = {}
            )
            ForwardingRuleEditorPhoneAddressEntry(
                phoneAddress = "+79135849638",
                onRemovePhoneAddress = {}
            )
            ForwardingRuleEditorPhoneAddressEntry(
                phoneAddress = "900",
                onRemovePhoneAddress = {}
            )
            ForwardingRuleEditorPhoneAddressEntry(
                phoneAddress = "SomeCompany",
                onRemovePhoneAddress = {}
            )
        }
    }
}
