package EntreApps.Shared.Modules.Base

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import org.koin.dsl.module

val appDatabase = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
}

val classes_NewProtoPatterns = module {
    single { FragmentNavigationHandler_NewProto() }
    single { PanelsGroupeButtonHandler() }
}

val modules_NewProtoPatterns = module {
    includes(
        appDatabase,
        classes_NewProtoPatterns,
    )
}
