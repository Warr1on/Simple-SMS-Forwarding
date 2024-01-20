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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplemobiletools.smsmessenger.R
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.model.MessageForwardingState

@Composable
fun MessageForwardingInfoView(
    status: MessageForwardingState
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        MessageForwardingDisclosedStatusView(status = status)
    }
}

@Composable
private fun MessageForwardingDisclosedStatusView(
    status: MessageForwardingState
) {
    when (status) {

        MessageForwardingState.CurrentlyForwarding -> TODO()

        is MessageForwardingState.FailedToForward -> TODO()

        is MessageForwardingState.ForwardedSuccessfully -> {
            val forwardedToText = remember(status) { textForForwardedToField(status = status) }
            MessageForwardingInfoColumn(
                iconID = R.drawable.ic_people_group,
                infoText = forwardedToText,
                contentDescription = ""
            )
        }

        is MessageForwardingState.PartialSuccess -> {
            MessageForwardingInfoColumn(iconID = R.drawable.ic_warning, infoText = "", contentDescription = "")
        }
    }
}

@Composable
private fun MessageForwardingInfoColumn(
    iconID: Int,
    infoText: String,
    contentDescription: String
) {
    Row {

        Image(
            painter = painterResource(id = iconID),
            contentDescription = contentDescription
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            infoText,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun textForForwardedToField(status: MessageForwardingState.ForwardedSuccessfully): String {

    if (status.numberOfRecipients > 5) {
        return "Forwarded to ${status.numberOfRecipients}"
    }

    val forwardedToTextPart = if (status.numberOfRecipients == 1) {
        "Forwarded to "
    } else {
        "Forwarded to ${status.numberOfRecipients} people: "
    }

    var namesTextPart = "\n"
    status.recipientNames.forEachIndexed { index, name ->
        if (index > 0) {
            namesTextPart += ", "
        }
        namesTextPart += name
    }

    return forwardedToTextPart + namesTextPart
}

@Preview
@Composable
fun MessageForwardingInfoView_Preview() {
    val status = MessageForwardingState.ForwardedSuccessfully(3, listOf("John A.", "Mary E.", "Trevor B."))
    AppTheme {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)) {
            MessageForwardingInfoView(status = status)
        }
    }
}
