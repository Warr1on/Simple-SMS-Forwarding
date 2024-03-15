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
import ru.warr1on.simplesmsforwarding.domain.services.messageForwarding.MessageInitialProcessingWorker

val DI.Modules.domain get() = module {
    includes(
        DI.Modules.repositories,
        DI.Modules.messageForwardingService
    )
}

private val DI.Modules.repositories get() = module {

    single {
        ForwardingRulesRepository.Factory.getDefaultRepo(
            rulesDao = get(),
            coroutineScope = get(named(DI.Qualifiers.Coroutines.generalIOCoroutineScope))
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
        MessageForwardingService(workManager = get())
    }

    worker {
        MessageInitialProcessingWorker(
            rulesRepo = get(),
            recordsRepo = get(),
            settingsRepo = get(),
            workManager = get(),
            context = androidApplication(),
            workerParameters = get()
        )
    }

    worker {
        MessageForwardingWorker(
            recordsRepo = get(),
            settingsRepo = get(),
            backendServiceRepo = get(),
            context = androidApplication(),
            workerParameters = get()
        )
    }
}
