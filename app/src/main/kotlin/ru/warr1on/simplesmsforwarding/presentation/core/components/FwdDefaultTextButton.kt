package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * A simple text button.
 * Has a ripple effect on touch built-in.
 *
 * @param text Text of the button
 * @param enabled Is the button enabled. Depending on that param, it will be
 * clickable or not, and it will have a different color for the disabled state
 * @param color Color of the text. Primary by default
 * @param fontWeight Font weight of the text. Normal by default
 * @param modifier An optional modifier for the button
 * @param onClick Will be executed on the button click
 */
@Composable
fun FwdDefaultTextButton(
    text: String,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    fontWeight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val ripple = rememberRipple()
    val interactionSource = remember { MutableInteractionSource() }

    val colorToUse = remember(color, enabled) {
        when (enabled) {
            true -> color
            false -> color.copy(alpha = 0.38f)
        }
    }
    val textColor by animateColorAsState(targetValue = colorToUse, label = "btn_txt_color")

    val containerModifier = when (enabled) {
        true -> {
            modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple,
                    onClick = onClick
                )
                .padding(horizontal = 8.dp, vertical = 8.dp)
        }
        false -> {
            modifier
                .clip(MaterialTheme.shapes.small)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        }
    }

    Box(modifier = containerModifier) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = fontWeight,
            color = textColor
        )
    }
}


//--- Previews ---//

@Preview
@Composable
private fun FwdDefaultTextButton_Preview() {
    AppTheme {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .border(width = 1.dp, color = Color.Gray)
                .padding(8.dp)
        ) {
            FwdDefaultTextButton(
                text = "Cancel",
                color = MaterialTheme.colorScheme.error,
                onClick = {}
            )

            Spacer(16.dp)

            FwdDefaultTextButton(
                text = "Confirm",
                color = MaterialTheme.colorScheme.primary,
                onClick = {}
            )
        }
    }
}
