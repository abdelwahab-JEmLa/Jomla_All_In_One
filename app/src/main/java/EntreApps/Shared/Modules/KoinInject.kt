package EntreApps.Shared.Modules

import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appDatabase = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
}

val viewModel_NewProtoPatterns = module {
    viewModel { ViewModel_NewProtoPatterns(androidContext(), get(), get()) }
}
val classes_NewProtoPatterns = module {
    single { FragmentNavigationHandler() }
}

val modules_NewProtoPatterns = module {
    includes(
        appDatabase,
        classes_NewProtoPatterns,
        viewModel_NewProtoPatterns
    )
}
