package ru.warr1on.simplesmsforwarding.core.di

import android.content.Context
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.warr1on.simplesmsforwarding.data.local.localData
import ru.warr1on.simplesmsforwarding.data.remote.networking
import ru.warr1on.simplesmsforwarding.domain.domain

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
            workManagerFactory()
            modules(
                Modules.core,
                Modules.domain
            )
        }
    }
}

val DI.Modules.core: Module
    get() {
        return module {
            includes(
                DI.Modules.localData,
                DI.Modules.networking,
                DI.Modules.coroutines,
                DI.Modules.work
            )
        }
    }


//--- Coroutines ---//

val DI.Qualifiers.Coroutines get() = CoroutineQualifiers
object CoroutineQualifiers {

    // Coroutine scopes
    const val generalIOCoroutineScope = "coroutine_scope_general_io"

    // Coroutine dispatchers
    const val defaultDispatcher = "coroutine_dispatcher_default"
    const val mainDispatcher = "coroutine_dispatcher_main"
    const val ioDispatcher = "coroutine_dispatcher_io"
}

val DI.Modules.coroutines: Module get() = module {

    val qualifiers = DI.Qualifiers.Coroutines

    single(named(qualifiers.generalIOCoroutineScope)) {
        CoroutineScope(get<CoroutineDispatcher>(named(qualifiers.ioDispatcher)) + SupervisorJob())
    }

    single(named(qualifiers.defaultDispatcher)) {
        Dispatchers.Default
    }

    single(named(qualifiers.mainDispatcher)) {
        Dispatchers.Main
    }

    single(named(qualifiers.ioDispatcher)) {
        Dispatchers.IO
    }
}

val DI.Modules.work: Module get() = module {

    factory {
        WorkManager.getInstance(androidApplication())
    }
}
