package ru.warr1on.simplesmsforwarding.core

import android.content.Context
import ru.warr1on.simplesmsforwarding.core.di.DI

class ForwarderAppCore {

    fun initializeForwarderApp(context: Context) {
        DI.startKoin(context)
    }
}
