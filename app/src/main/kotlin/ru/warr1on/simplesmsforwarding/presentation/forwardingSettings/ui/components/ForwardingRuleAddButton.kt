package ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplemobiletools.smsmessenger.R
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultPresetCard
import ru.warr1on.simplesmsforwarding.presentation.core.components.Spacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

@Composable
fun ForwardingRuleAddButton(
    onClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    val ripple = rememberRipple()

    FwdDefaultPresetCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple
                ) {
                    onClick()
                }
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Text(
                "Add new rule",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer()

            Image(
                painter = painterResource(id = R.drawable.ic_add_circle_filled),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ForwardingRuleCreateButton_Preview() {
    AppTheme {
        Box(
            Modifier
                .background(Color.White)
                .padding(16.dp)) {
            ForwardingRuleAddButton() {}
        }
    }
}
