package ru.warr1on.simplesmsforwarding.presentation

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module
import ru.warr1on.simplesmsforwarding.core.di.DI
import ru.warr1on.simplesmsforwarding.presentation.forwardingMainScreen.ForwardingMainScreenViewModel
import ru.warr1on.simplesmsforwarding.presentation.forwardingSettings.ForwardingSettingsViewModel

val DI.Modules.presentation: Module get() = module {
    includes(
        mainScreenModule,
        settingsScreenModule
    )
}

private val mainScreenModule: Module get() = module {
    viewModelOf(::ForwardingMainScreenViewModel)
}

private val settingsScreenModule: Module get() = module {
    viewModelOf(::ForwardingSettingsViewModel)
}
