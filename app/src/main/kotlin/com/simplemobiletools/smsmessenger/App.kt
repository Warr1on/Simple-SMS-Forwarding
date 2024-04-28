package com.simplemobiletools.smsmessenger

import android.app.Application
import com.simplemobiletools.commons.extensions.checkUseEnglish
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import ru.warr1on.simplesmsforwarding.core.di.DI
import ru.warr1on.simplesmsforwarding.core.di.core

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()

        // Forwarder-specific DI initialization
        DI.startKoin(this)
    }
}
