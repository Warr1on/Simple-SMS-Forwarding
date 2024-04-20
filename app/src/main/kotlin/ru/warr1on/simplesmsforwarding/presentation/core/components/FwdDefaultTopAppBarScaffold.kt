package ru.warr1on.simplesmsforwarding.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

/**
 * A scaffold with the default preset for the forwarder app.
 *
 * Has a top app bar with the "show background on scroll" behavior.
 *
 * @param navbarTitle The title of the top bar
 * @param navigationIcon Navigation icon for the left side of the top bar
 * @param actions Action buttons on the right side of the top bar
 * @param content Content of the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FwdDefaultTopAppBarScaffold(
    navbarTitle: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(navbarTitle) },
                navigationIcon = navigationIcon,
                actions = actions,
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


//--- Previews ---//

@Preview
@Composable
private fun FwdDefaultNavbarScaffold_Preview() {
    AppTheme {
        FwdDefaultTopAppBarScaffold(
            navbarTitle = "Some screen",
            navigationIcon = {
                FwdDefaultIconButton(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    onClick = {}
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("CONTENT", fontSize = 24.sp)
            }
        }
    }
}
