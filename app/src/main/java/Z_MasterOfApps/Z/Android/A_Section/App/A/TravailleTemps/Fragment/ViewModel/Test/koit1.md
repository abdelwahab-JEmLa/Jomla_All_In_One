package Z_MasterOfApps.Z.Android.A.Main

import Z_CodePartageEntreApps.Model.A_ProduitModelRepository
import Z_CodePartageEntreApps.Model.A_ProduitModelRepositoryImpl
import Z_CodePartageEntreApps.Model.CategoriesRepositoryImpl
import Z_CodePartageEntreApps.Model.H_GroupesCategoriesRepository
import Z_CodePartageEntreApps.Model.H_GroupesCategoriesRepositoryImpl
import Z_CodePartageEntreApps.Model.I_CategoriesRepository
import Z_CodePartageEntreApps.Model.J_AppInstalleDonTelephoneRepository
import Z_CodePartageEntreApps.Model.J_AppInstalleDonTelephoneRepositoryImpl
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.A.Main.C_EcranDeDepart.Startup.B2.Windows.ViewModelW4
import Z_MasterOfApps.Z.Android.Base.App.Sections.BaseDonneEdite.FragID_1_DialogeCategoryReorderAndSelectionWindow.ViewModel.ViewModel_A4FragID1
import Z_MasterOfApps.Z_AppsFather.Kotlin.HelloWorldLearn.A_KoinProto.EcranDepartApp.Navigator
import Z_MasterOfApps.Z_AppsFather.Kotlin.HelloWorldLearn.A_KoinProto.EcranDepartApp.ViewModel.Coordinator
import Z_MasterOfApps.Z_AppsFather.Kotlin.HelloWorldLearn.A_KoinProto.EcranDepartApp.ViewModel.FragmentViewModel
import Z_CodePartageEntreApps.SectionApp.New.App.TravailleTemps.ViewModel.Windows__ViewModel
import Z_MasterOfApps._WorkingON.id4_DeplaceProduitsVerGrossist.ViewModel.FragID_4_App1_ViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Module pour les repositories
val repositoryModule = module {
    // Singleton: une seule instance pour toute l'application
    single<A_ProduitModelRepository> { A_ProduitModelRepositoryImpl() }

    single<I_CategoriesRepository> { CategoriesRepositoryImpl() }
    single<H_GroupesCategoriesRepository> { H_GroupesCategoriesRepositoryImpl() }
    single<J_AppInstalleDonTelephoneRepository> { J_AppInstalleDonTelephoneRepositoryImpl() }
// In your module definition
single { Z_CodePartageEntreApps.Model.K_TempTravailleRepository() }
}

// Module pour les ViewModels
val viewModelModule = module {
    viewModel { FragmentViewModel(get(), get(), get()) }
    viewModel { ViewModelInitApp() }
    viewModel { ViewModel_A4FragID1(get(), get(), get()) }
    viewModel { ViewModelW4(get()) }
    viewModel { FragID_4_App1_ViewModel() }
    viewModel { Windows__ViewModel(get()) }
}

val coordinatorModule = module {
    factory { (navigator: Navigator) -> Coordinator(get(), navigator) }
}

// Module principal qui regroupe tous les autres modules
val appModule = module {
    // Inclure d'autres modules dans l'ordre correct
    includes(repositoryModule, viewModelModule, coordinatorModule)
}
