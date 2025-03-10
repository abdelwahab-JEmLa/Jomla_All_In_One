package com.example.clientjetpack

import Z_MasterOfApps.Kotlin.Model.A_ProduitModelRepository
import Z_MasterOfApps.Kotlin.Model.A_ProduitModelRepositoryImpl
import Z_MasterOfApps.Kotlin.Model.CategoriesRepositoryImpl
import Z_MasterOfApps.Kotlin.Model.H_GroupesCategoriesRepository
import Z_MasterOfApps.Kotlin.Model.H_GroupesCategoriesRepositoryImpl
import Z_MasterOfApps.Kotlin.Model.I_CategoriesRepository
import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepository
import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepositoryImpl
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_.ConnectionManager
import android.content.Context
import com.example.clientjetpack.Modules.AppDatabase
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Module pour les repositories
val repositoryModule = module {
    // Singleton: une seule instance pour toute l'application
    single<A_ProduitModelRepository> { A_ProduitModelRepositoryImpl() }

    single<I_CategoriesRepository> { CategoriesRepositoryImpl() }
    single<H_GroupesCategoriesRepository> { H_GroupesCategoriesRepositoryImpl() }
    single<J_AppInstalleDonTelephoneRepository> { J_AppInstalleDonTelephoneRepositoryImpl() }
    // Database singleton using the nested DatabaseModule
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
}

// Module pour les ViewModels
val viewModelModule = module {
    viewModel { ViewModelInitApp() }
    viewModel { (context: Context) -> HeadViewModel(get(), AppDatabase.DatabaseModule.getDatabase(get())) }
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
