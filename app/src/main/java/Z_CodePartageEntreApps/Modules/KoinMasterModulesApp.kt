package Z_CodePartageEntreApps.Modules

import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepositoryImpl
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepositoryImpl
import Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository.B_ClientsDataBaseRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository.B_ClientsDataBaseRepositoryImpl
import Z_CodePartageEntreApps.Model.CategoriesRepositoryImpl
import Z_CodePartageEntreApps.Model.H_GroupesCategoriesRepository
import Z_CodePartageEntreApps.Model.H_GroupesCategoriesRepositoryImpl
import Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository.I_CategorieProduitsRepository
import Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository.I_CategorieProduitsRepositoryImpl
import Z_CodePartageEntreApps.Model.I_CategoriesRepository
import Z_CodePartageEntreApps.Model.J_AppInstalleDonTelephoneRepository
import Z_CodePartageEntreApps.Model.J_AppInstalleDonTelephoneRepositoryImpl
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.K_TempTravailleRepositoryImpl
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_.ConnectionManager
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.ViewModel_App2FragID1
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import android.content.Context
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Module pour les repositories
val repositoryModule = module {
    single<B_ClientDataBaseRepository> { B_ClientDataBaseRepositoryImpl(get()) }

    single<A_ProduitRepository> { A_ProduitRepositoryImpl(get()) }

    single<B_ClientsDataBaseRepository> { B_ClientsDataBaseRepositoryImpl() }

    single<I_CategoriesRepository> { CategoriesRepositoryImpl() }
    single<I_CategorieProduitsRepository> { I_CategorieProduitsRepositoryImpl(get()) }

    single<H_GroupesCategoriesRepository> { H_GroupesCategoriesRepositoryImpl() }
    single<J_AppInstalleDonTelephoneRepository> { J_AppInstalleDonTelephoneRepositoryImpl() }
    single<K_TempTravailleRepository> { K_TempTravailleRepositoryImpl() }

    // Database singleton using the nested DatabaseModule
    single { AppDatabase.DatabaseModule.getDatabase(get()) }

}

val viewModelModule = module {
    viewModel { ViewModelInitApp(
        get(),get()
    ) }
    viewModel { (context: Context) -> HeadViewModel(get(),
        AppDatabase.DatabaseModule.getDatabase(get())
    ) }
    viewModel { Windows__ViewModel(get()) }
    viewModel { ViewModel_App2FragID1(get()) }
}

val servicesModule = module {
    // Create a factory for ConnectionManager that takes HeadViewModel as a parameter
    factory { (viewModel: HeadViewModel, context: Context) ->
        ConnectionManager(
            context = context,
        )
    }

}

// Update appModule to include the servicesModule
val appModule = module {
    includes(repositoryModule, servicesModule, viewModelModule)
}
