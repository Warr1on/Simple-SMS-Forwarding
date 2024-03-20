package ru.warr1on.simplesmsforwarding.presentation.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import ru.warr1on.simplesmsforwarding.presentation.core.components.modal.ModalHostExperimental
import ru.warr1on.simplesmsforwarding.presentation.core.navigation.NavigationContainer
import ru.warr1on.simplesmsforwarding.presentation.core.theme.AppTheme

class MessageForwardingActivity : AppCompatActivity() {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            KoinAndroidContext {
                AppTheme {
                    ModalHostExperimental {
                        NavigationContainer()
                    }
                }
            }
        }
    }
}

