package ru.warr1on.simplesmsforwarding.data.local

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.warr1on.simplesmsforwarding.core.di.Coroutines
import ru.warr1on.simplesmsforwarding.core.di.DI

/**
 * A DI module that contains core dependencies related to local data and persistence
 */
val DI.Modules.localData: Module
    get() {
        return module {

            // Forwarder database
            single {
                Room
                    .databaseBuilder(
                        androidApplication(),
                        ForwardingDatabase::class.java,
                        ForwardingDatabase.databaseName
                    )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            factory {
                get<ForwardingDatabase>().forwardingRulesDao()
            }

            factory {
                get<ForwardingDatabase>().forwardingRequestDao()
            }

            single {
                LocalDataStore.Factory.getDefaultDataStore(
                    context = androidApplication(),
                    coroutineScope = get(named(DI.Qualifiers.Coroutines.generalIOCoroutineScope))
                )
            }
        }
    }
