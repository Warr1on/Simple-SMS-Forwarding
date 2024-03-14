package ru.warr1on.simplesmsforwarding.domain

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.warr1on.simplesmsforwarding.core.di.Coroutines
import ru.warr1on.simplesmsforwarding.core.di.DI
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwarderSettingsRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.ForwardingRulesRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.FwdbotServiceRepository
import ru.warr1on.simplesmsforwarding.domain.repositories.MessageForwardingRecordsRepository
import ru.warr1on.simplesmsforwarding.domain.services.messageForwarding.MessageForwardingService
import ru.warr1on.simplesmsforwarding.domain.services.messageForwarding.MessageForwardingWorker

//object DomainModules

val DI.Modules.domain get() = module {
    includes(
        DI.Modules.repositories,
        DI.Modules.messageForwardingService
//        DI.Modules.services
    )
}

private val DI.Modules.repositories get() = module {

    single {
        ForwardingRulesRepository.Factory.getDefaultRepo(
            rulesDao = get(),
            parentScope = get(),
            ioDispatcher = get()
        )
    }

    single {
        ForwarderSettingsRepository.Factory.getDefaultRepo(
            dataStore = get(),
            coroutineScope = get()
        )
    }

    single {
        FwdbotServiceRepository.Factory.getDefaultRepo(get())
    }

    single {
        MessageForwardingRecordsRepository.Factory.getDefaultRepo(get(), get(), get())
    }
}

private val DI.Modules.messageForwardingService get() = module {

    single {
        MessageForwardingService(
            //recordsRepo = get(),
            rulesRepo = get(),
            settingsRepo = get(),
            //backendServiceRepo = get(),
            //appContext = androidApplication(),
            coroutineScope = get(named(DI.Qualifiers.Coroutines.generalIOCoroutineScope))
        )
    }

    worker {
        MessageForwardingWorker(
            backendServiceRepo = get(),
            context = androidApplication(),
            workerParameters = get()
        )
    }
}

//private val DI.Modules.services get() = module {
//
//}
