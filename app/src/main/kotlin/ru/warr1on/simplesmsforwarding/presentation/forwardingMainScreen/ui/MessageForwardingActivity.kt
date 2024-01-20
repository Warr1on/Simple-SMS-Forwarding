package ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalHostExperimental
import ru.warr1on.simplesmsforwarding.presentation.core.navigation.NavigationContainer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

class MessageForwardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTheme {
                ModalHostExperimental {
//                    MessageForwardingScreen()
                    NavigationContainer()
                }
            }
        }
    }
}

