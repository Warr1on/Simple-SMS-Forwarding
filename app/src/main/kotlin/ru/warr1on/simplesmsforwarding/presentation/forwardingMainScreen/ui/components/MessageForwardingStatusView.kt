package ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplemobiletools.smsmessenger.R
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.model.MessageForwardingState
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.model.MessageForwardingState.*

@Composable
fun MessageForwardingStatusView(currentStatus: MessageForwardingState) {

    val statusText = remember(currentStatus) { statusText(currentStatus) }
    val statusIconID = remember(currentStatus) { statusIconID(currentStatus) }

    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(
            statusText,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.width(4.dp))

        Image(
            painter = painterResource(id = statusIconID),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(statusIconColor(status = currentStatus)),
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun statusText(status: MessageForwardingState): String {
    return when (status) {
        CurrentlyForwarding -> "Forwarding..."
        is FailedToForward -> "Failed"
        is ForwardedSuccessfully -> "${status.numberOfRecipients}"
        is PartialSuccess -> ""
    }
}

private fun statusIconID(status: MessageForwardingState): Int {
    return when (status) {
        CurrentlyForwarding -> R.drawable.ic_upload
        is FailedToForward -> R.drawable.ic_error
        is ForwardedSuccessfully -> R.drawable.ic_people_group
        is PartialSuccess -> R.drawable.ic_warning
    }
}

@Composable
private fun statusIconColor(status: MessageForwardingState): Color {
    return when (status) {
        CurrentlyForwarding -> MaterialTheme.colorScheme.onSurface
        is FailedToForward -> MaterialTheme.colorScheme.error
        is ForwardedSuccessfully -> MaterialTheme.colorScheme.onSurface
        is PartialSuccess -> MaterialTheme.colorScheme.error
    }
}

@Preview
@Composable
fun MessageForwardingStatusView_Preview() {
    AppTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 2.dp, horizontal = 8.dp)
        ) {
            MessageForwardingStatusView(currentStatus = CurrentlyForwarding)
        }
    }
}
