package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepositoryImpl
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepositoryImpl
import Z_CodePartageEntreApps.Model.C_GrossistsDataBaseRepository.C_GrossistsDataBaseRepository
import Z_CodePartageEntreApps.Model.C_GrossistsDataBaseRepository.C_GrossistsDataBaseRepositoryImpl
import Z_CodePartageEntreApps.Model.CategoriesRepositoryImpl
import Z_CodePartageEntreApps.Model.H_GroupesCategoriesRepository
import Z_CodePartageEntreApps.Model.H_GroupesCategoriesRepositoryImpl
import Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository.I_CategorieProduitsRepository
import Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository.I_CategorieProduitsRepositoryImpl
import Z_CodePartageEntreApps.Model.I_CategoriesRepository
import Z_CodePartageEntreApps.Model.J_AppInstalleDonTelephoneRepository
import Z_CodePartageEntreApps.Model.J_AppInstalleDonTelephoneRepositoryImpl
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.Model.K_TempTravailleRepository.Repository.K_TempTravailleRepositoryImpl
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.SoldArticlesTabelleRepository
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.SoldArticlesTabelleRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchatRepository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchatRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentRepository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentRepositoryImpl
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Common repositories that are used by both app types
val commonRepositoriesModule = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }

    single<_1_1_CouleurAcheteOperationRepository> { _1_1_CouleurAcheteOperationRepositoryImpl(get()) }
    single<_1_2_ProduitAcheteOperationRepository> { _1_2_ProduitAcheteOperationRepositoryImpl(get()) }
    single<_1_3_BonAchatRepository> { _1_3_BonAchatRepositoryImpl(get()) }
    single<_1_4_PeriodeVentRepository> { _1_4_PeriodeVentRepositoryImpl(get()) }

    single<B_ClientDataBaseRepository> { B_ClientDataBaseRepositoryImpl(get()) }
    single<B_ClientDataBaseRepository> { B_ClientDataBaseRepositoryImpl(get()) }
    single<A_ProduitRepository> { A_ProduitRepositoryImpl(get()) }
    single<I_CategoriesRepository> { CategoriesRepositoryImpl() }
    single<I_CategorieProduitsRepository> { I_CategorieProduitsRepositoryImpl(get()) }
    single<K_TempTravailleRepository> { K_TempTravailleRepositoryImpl() }
    single<H_GroupesCategoriesRepository> { H_GroupesCategoriesRepositoryImpl() }
    single<J_AppInstalleDonTelephoneRepository> { J_AppInstalleDonTelephoneRepositoryImpl() }
    single<SoldArticlesTabelleRepository> { SoldArticlesTabelleRepositoryImpl() }
    single<C_GrossistsDataBaseRepository> { C_GrossistsDataBaseRepositoryImpl() }

    viewModel { ViewModelInitApp(
        get(),get()
    ) }
}

// Function to determine the application type
fun isManagerApp(context: Context): Boolean {
    return context.packageName == "com.example.abdelwahabjemlajetpack.serveur"
}

val appTypeModule = module {
    single {
        val appType = if (isManagerApp(get())) "manager" else "client"
        appType
    }
}

val appModule = module {
    includes(commonRepositoriesModule, appTypeModule)

    single {
        val context = get<Context>()
        if (isManagerApp(context)) {
            // Return some manager-specific instance if needed
            "manager-context"
        } else {
            // Return some client-specific instance if needed
            "client-context"
        }
    }
}
