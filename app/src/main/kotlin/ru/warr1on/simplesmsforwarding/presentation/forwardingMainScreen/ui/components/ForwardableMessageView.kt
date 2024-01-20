package ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.components.FwdDefaultPresetCard
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.model.MessageForwardingState

@Composable
fun ForwardableMessageView() {

    FwdDefaultPresetCard(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        MessageContentView(
            messageAddress = "+79534426896",
            messageText = "Lorem ipsum dolor amet. Dolorem veniam quisquam ut officiis qui nihil et optio. Exercitationem ducimus facere dolores."
        )
    }

//    val cardBorder: BorderStroke? = if (isSystemInDarkTheme()) {
//        BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
//    } else {
//        null
//    }
//
//    val cardColors = if (!isSystemInDarkTheme()) {
//        CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface,
//            contentColor = MaterialTheme.colorScheme.onSurface
//        )
//    } else {
//        CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.secondaryContainer,
//            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
//        )
//    }
//
//    val cardElevation: CardElevation = if (!isSystemInDarkTheme()) {
//        CardDefaults.cardElevation(
//            defaultElevation = 2.dp
//        )
//    } else {
//        CardDefaults.cardElevation(
//            defaultElevation = 0.dp
//        )
//    }

//    Card(
//        shape = RoundedCornerShape(12.dp),
//        border = cardBorder,
//        colors = cardColors,
//        elevation = cardElevation,
//        modifier = Modifier
//            .padding(vertical = 8.dp, horizontal = 16.dp)
//            .fillMaxWidth()
//    ) {
//        MessageContentView(
//            messageAddress = "+79534426896",
//            messageText = "Lorem ipsum dolor amet. Dolorem veniam quisquam ut officiis qui nihil et optio. Exercitationem ducimus facere dolores."
//        )
//    }
}

@Composable
private fun MessageContentView(
    messageAddress: String,
    messageText: String
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                messageAddress,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(0.1f))

            MessageForwardingStatusView(currentStatus = MessageForwardingState.CurrentlyForwarding)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            messageText,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 15.sp
        )
    }
}

@Preview
@Composable
fun ForwardableMessageView_Preview() {
    AppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            ForwardableMessageView()
        }
    }
}

@Preview
@Composable
fun MessageContentView_Preview() {
    AppTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            MessageContentView(
                messageAddress = "+79534426896",
                messageText = "Lorem ipsum dolor amet. Dolorem veniam quisquam ut officiis qui nihil et optio. Exercitationem ducimus facere dolores."
            )
        }
    }
}
