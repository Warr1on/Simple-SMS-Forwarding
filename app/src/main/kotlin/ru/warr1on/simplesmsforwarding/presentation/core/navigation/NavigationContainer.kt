package ru.warr1on.simplesmsforwarding.presentation.core.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ui.MessageForwardingScreen
import ru.warr1on.simplesmsforwarding.presentation.forwardingRuleEditor.ui.forwardingRuleEditorScreen
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ui.ForwardingSettingsScreen

@Composable
fun NavigationContainer() {

    val navController = rememberNavController()
    val backgroundColor = MaterialTheme.colorScheme.background

    NavHost(
        navController = navController,
        startDestination = "forwarding-main",
        enterTransition = { fadeIn() + slideInHorizontally { it } },
        exitTransition = { fadeOut() + slideOutHorizontally { it } },
        popEnterTransition = { fadeIn() + slideInHorizontally { -it / 3 }},
        modifier = Modifier.fillMaxSize().background(backgroundColor)
    ) {

        composable(
            route = "forwarding-main",
            enterTransition = { fadeIn() + slideInHorizontally { -it / 3 }},
            exitTransition = { fadeOut() + slideOutHorizontally { -it / 3 } }
        ) {
            MessageForwardingScreen(
                onNavigateToSettings = {
                    navController.navigate("forwarding-settings")
                }
            )
        }

        composable(
            route = "forwarding-settings",
            exitTransition = { fadeOut() + scaleOut(targetScale = 0.9f, animationSpec = spring()) },
            popEnterTransition = { fadeIn() + scaleIn(initialScale = 0.9f, animationSpec = spring()) },
            popExitTransition = { fadeOut() + slideOutHorizontally { it } }
        ) {
            ForwardingSettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onRuleEditorOpenRequest = {
                    navController.navigate("fwd-rule-editor?ruleId=$it")
                }
            )
        }

        forwardingRuleEditorScreen(
            onCompletion = {
                navController.popBackStack()
            }
        )
    }
}
