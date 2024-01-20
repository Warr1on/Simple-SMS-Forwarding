package ru.warr1on.simplesmsforwarding.core.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.warr1on.simplesmsforwarding.data.local.localData
import ru.warr1on.simplesmsforwarding.data.remote.networking

/**
 * A central point to access all the DI stuff, like modules and qualifiers.
 */
object DI {

    /**
     * A place to access the DI modules.
     *
     * Modules are added here by extending [DI.Modules]
     */
    object Modules

    /**
     * A place to access the DI qualifiers.
     *
     * Qualifiers are added here by extending [DI.Qualifiers]
     */
    object Qualifiers

    fun startKoin(context: Context) {
        org.koin.core.context.startKoin {
            androidLogger()
            androidContext(context)
            modules(Modules.core)
        }
    }
}

val DI.Modules.core: Module
    get() {
        return module {
            includes(
                DI.Modules.localData,
                DI.Modules.networking
            )
        }
    }
