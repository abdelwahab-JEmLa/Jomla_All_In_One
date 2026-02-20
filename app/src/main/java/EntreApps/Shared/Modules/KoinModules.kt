package EntreApps.Shared.Modules

import org.koin.dsl.module

val appDatabase = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
}
