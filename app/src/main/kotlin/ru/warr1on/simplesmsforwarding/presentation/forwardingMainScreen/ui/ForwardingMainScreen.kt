package ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.simplemobiletools.smsmessenger.R
import org.koin.androidx.compose.koinViewModel
import ru.warr1on.simplesmsforwarding.presentation.core.extensions.verticalSpacer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ForwardingMainScreenViewModel
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ui.components.ForwardableMessageView

/**
 * Main forwarder screen composable
 */
@Composable
fun MessageForwardingScreen(
    viewModel: ForwardingMainScreenViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit
) {
    ForwardingScreenMainLayout(
        onNavigateToSettings = onNavigateToSettings
    )
}

/**
 * A composed full layout of the main forwarder screen
 */
@Composable
private fun ForwardingScreenMainLayout(
    onNavigateToSettings: () -> Unit
) {
    ForwardingScreenScaffold(onNavigateToSettings = onNavigateToSettings) { paddingValues ->
        ForwardingRecordsListView(
            topPadding = 12.dp + paddingValues.calculateTopPadding(),
            bottomPadding = 12.dp + paddingValues.calculateBottomPadding()
        )
    }
}

/**
 * A scaffold with the top app bar for the main forwarder screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForwardingScreenScaffold(
    onNavigateToSettings: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Forwarding") },
                navigationIcon =  {
                    val context = LocalContext.current
                    IconButton(
                        onClick = {
                            if (context is AppCompatActivity) context.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Forwarding settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Composable
private fun ForwardingRecordsListView(
    topPadding: Dp,
    bottomPadding: Dp
) {
    LazyColumn {
        verticalSpacer(topPadding)
        items(count = 10) {
            ForwardableMessageView()
        }
        verticalSpacer(bottomPadding)
    }
}


// --- Previews ---//

@Preview
@Composable
private fun MessageForwardingScreenPreview() {
    AppTheme {
        ForwardingScreenMainLayout {}
    }
}
