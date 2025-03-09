package com.example.clientjetpack

import Z_MasterOfApps.Kotlin.Model.A_ProduitModelRepository
import Z_MasterOfApps.Kotlin.Model.A_ProduitModelRepositoryImpl
import Z_MasterOfApps.Kotlin.Model.CategoriesRepositoryImpl
import Z_MasterOfApps.Kotlin.Model.H_GroupesCategoriesRepository
import Z_MasterOfApps.Kotlin.Model.H_GroupesCategoriesRepositoryImpl
import Z_MasterOfApps.Kotlin.Model.I_CategoriesRepository
import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepository
import Z_MasterOfApps.Kotlin.Model.J_AppInstalleDonTelephoneRepositoryImpl
import org.koin.dsl.module

// Module pour les repositories
val repositoryModule = module {
    // Singleton: une seule instance pour toute l'application
    single<A_ProduitModelRepository> { A_ProduitModelRepositoryImpl() }

    single<I_CategoriesRepository> { CategoriesRepositoryImpl() }
    single<H_GroupesCategoriesRepository> { H_GroupesCategoriesRepositoryImpl() }
    single<J_AppInstalleDonTelephoneRepository> { J_AppInstalleDonTelephoneRepositoryImpl() }
}

// Module pour les ViewModels
val viewModelModule = module {
}


// Module principal qui regroupe tous les autres modules
val appModule = module {
    // Inclure d'autres modules dans l'ordre correct
    includes(repositoryModule, viewModelModule)
}
