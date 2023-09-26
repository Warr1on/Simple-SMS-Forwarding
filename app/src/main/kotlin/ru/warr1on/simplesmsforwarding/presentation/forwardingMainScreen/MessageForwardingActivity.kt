package ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.example.compose.AppTheme

class MessageForwardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MessageForwardingScreen()
            }
        }
    }
}

