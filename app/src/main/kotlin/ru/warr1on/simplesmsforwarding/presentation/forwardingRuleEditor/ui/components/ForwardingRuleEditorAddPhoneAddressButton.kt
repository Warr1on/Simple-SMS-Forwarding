package ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.warr1on.simplesmsforwarding.presentation.core.components.Spacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun ForwardingRuleEditorAddPhoneAddressButton(
    onAddNewAddressClicked: () -> Unit,
    onAddFromKnownClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        AddPhoneAddressButtonSegment(
            title = "Add number",
            onClick = onAddNewAddressClicked
        )

        Spacer(8.dp)

        VerticalDivider(
            thickness = 1.dp,
            modifier = Modifier.height(26.dp)
        )

        Spacer(8.dp)

        AddPhoneAddressButtonSegment(
            title = "Add from known",
            onClick = onAddFromKnownClicked
        )
    }
}

@Composable
private fun AddPhoneAddressButtonSegment(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ripple = rememberRipple()
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple,
                onClick = onClick
            )
            .padding(8.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


//--- Previews ---//

@Preview
@Composable
private fun ForwardingRuleEditorAddPhoneAddressButton_Preview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ForwardingRuleEditorAddPhoneAddressButton(
                onAddNewAddressClicked = {},
                onAddFromKnownClicked = {}
            )
        }
    }
}
