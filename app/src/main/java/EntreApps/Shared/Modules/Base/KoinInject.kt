package EntreApps.Shared.Modules.Base

import Application4.App.A.Start.Init.Proto.A_LoadingViewModel
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appDatabase = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
}

val AViewModel_NewProtoPatterns = module {
    viewModel { A_ViewModel_NewProtoPatterns(androidContext(), get(), get()) }
    viewModel {
        A_LoadingViewModel(
            appDatabase = get(),          // already bound as single { } in your DB module
            appContext = androidContext() // Koin's application context — never leaks Activity
        )
    }
}
val classes_NewProtoPatterns = module {
    single { FragmentNavigationHandler_NewProto() }
    single { PanelsGroupeButtonHandler() }
}

val modules_NewProtoPatterns = module {
    includes(
        appDatabase,
        classes_NewProtoPatterns,
        AViewModel_NewProtoPatterns
    )
}
