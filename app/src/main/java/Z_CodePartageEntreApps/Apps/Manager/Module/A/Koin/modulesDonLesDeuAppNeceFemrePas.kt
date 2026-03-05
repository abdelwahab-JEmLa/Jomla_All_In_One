package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import EntreApps.Shared.Modules.appDatabase
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Fragment.A.ViewModel.ViewModel_NewProtoPatterns
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val vmModel_NewProtoPatterns = module {
    viewModel { ViewModel_NewProtoPatterns(androidContext(), get(), get()) }
}
val classes_NeceFemrePas = module {
    single { FragmentNavigationHandler() }
}

val modulesDonLesDeuAppNeceFemrePas = module {
    includes(
        appDatabase,
        classes_NeceFemrePas,
        vmModel_NewProtoPatterns
    )
}
