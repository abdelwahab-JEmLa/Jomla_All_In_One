package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.ClientsMapFilterViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepositoryImpl
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.ViewModel.VendeursViewModel
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID1.VentHistoriques.Fragment.ViewModel.PeriodeVenteViewModel
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.TariffsButtonsViewModel_TestID2
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.FireBaseOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.RoomOperationsHandler
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.InfosSqlDataBasesRepository
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_RepositoryImpl
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
import Z_CodePartageEntreApps.Modules.ConnectionManager
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_Head_SQL_RepositorysImpl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_BonAchate_Repository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_BonAchate_RepositoryImp
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_VendeurRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_Repository
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_RepositoryImpl
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase_Repository
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase_RepositoryImpl
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_RepositoryImpl
import Z_CodePartageEntreApps.Windows.B.Windows.ViewModel.ViewModelFragment_StartUpScreen
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val itsProductionMode = false

val commonRepositoriesModule = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }

    single {
        InfosSqlDataBasesRepository(
            get(),
            get(),
            get(),
        )
    }

    single {
        FireBaseOperationsHandler()
    }

    single {
        RoomOperationsHandler(
            get(),
        )
    }
    single<_01_VentsHistoriquesDataBase_Repository> {
        _01_VentsHistoriquesDataBase_RepositoryImpl(itsProductionMode)
    }

    single<_0_0_HeadSQLRepositorys> {
        _0_0_Head_SQL_RepositorysImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    single<E1SecteurDeClientsRepository> {
        E1SecteurDeClientsRepositoryImpl(
            get(),
        )
    }

    single<_1_1_CouleurAcheteOperation_Repository> { _1_1_CouleurAcheteOperationRepositoryImpl(get()) }
    single<_1_2_ProduitAcheteOperation_Repository> { _1_2_ProduitAcheteOperationRepositoryImpl(get()) }
    single<C3_BonAchate_Repository> { C3_BonAchate_RepositoryImp(get()) }
    single<_1_4_PeriodeVent_Repository> { _1_4_PeriodeVentRepositoryImpl(get()) }
    single<_1_5_Vendeur_Repository> { _1_5_VendeurRepositoryImpl(get()) }

    single<_2_1_ProduitsDataBase_Repository> { _2_1_ProduitsDataBase_RepositoryImpl(get()) }

    single<_3_ClientsDataBase_Repository> { _3_ClientsDataBase_RepositoryImpl(get()) }
    single<_4_CouleurOperationCommand_Repository> {
        _4_CouleurOperationCommand_RepositoryImpl(get())
    }

    single<B_ClientDataBaseRepository> { B_ClientDataBaseRepositoryImpl(get()) }
    single<A_ProduitRepository> { A_ProduitRepositoryImpl(get()) }
    single<I_CategoriesRepository> { CategoriesRepositoryImpl() }
    single<I_CategorieProduitsRepository> { I_CategorieProduitsRepositoryImpl(get()) }
    single<K_TempTravailleRepository> { K_TempTravailleRepositoryImpl() }
    single<H_GroupesCategoriesRepository> { H_GroupesCategoriesRepositoryImpl() }
    single<J_AppInstalleDonTelephoneRepository> { J_AppInstalleDonTelephoneRepositoryImpl() }
    single<SoldArticlesTabelleRepository> { SoldArticlesTabelleRepositoryImpl() }
    single<C_GrossistsDataBaseRepository> { C_GrossistsDataBaseRepositoryImpl() }
}

val navigationModule = module {
    single { FragmentNavigationHandler() }
}

val viewModelModule = module {
    viewModel {
        TariffsButtonsViewModel_TestID2(
            get(),
            get(),
        )
    }


    viewModel {
        ViewModelMessageur(
            get(),
        )
    }

    factory { (viewModel: HeadViewModel, context: Context) ->
        ConnectionManager(
            context = context,
        )
    }

    viewModel { (context: Context) ->
        HeadViewModel(
            get(),
            AppDatabase.DatabaseModule.getDatabase(get())
        )
    }

    viewModel { Windows__ViewModel(get()) }

    // Original viewModels
    viewModel { PeriodeVenteViewModel(get()) }
    viewModel { ViewModelFragment_StartUpScreen(get(), get(), get(), get(), get()) }
    viewModel { ViewModelInitApp(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { VendeursViewModel(get(), get()) }
    viewModel {
        ViewModel_MapClients_App2FragID1(
            get(),
            get(),
            get(),
            get()
        )
    }

    // Update this ViewModel to use the navigation handler
    viewModel {
        ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
            get(),  // The repository
            get()   // The navigation handler
        )
    }
    viewModel {
        ClientsMapFilterViewModel(
            get(),  // The repository
        )
    }
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


val uiHandlersModule = module {
    single { PanelsGroupeButtonHandler() }
}

// Ensuite, incluez ce module dans votre appModule
val appModule = module {
    includes(
        commonRepositoriesModule,
        appTypeModule,
        viewModelModule,
        navigationModule,
        uiHandlersModule
    )

    // Rest of the code remains the same
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
