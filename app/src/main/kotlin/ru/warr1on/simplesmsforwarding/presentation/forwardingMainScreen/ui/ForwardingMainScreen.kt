package ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simplemobiletools.smsmessenger.R
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ui.components.ForwardableMessageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageForwardingScreen(
    onNavigateToSettings: () -> Unit
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
        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(12.dp + paddingValues.calculateTopPadding()))
            }
            items(count = 10) {
                ForwardableMessageView()
            }
            item {
                Spacer(modifier = Modifier.height(12.dp + paddingValues.calculateBottomPadding()))
            }
        }
    }
}

@Preview
@Composable
private fun MessageForwardingScreenPreview() {
    AppTheme {
        MessageForwardingScreen {}
    }
}
