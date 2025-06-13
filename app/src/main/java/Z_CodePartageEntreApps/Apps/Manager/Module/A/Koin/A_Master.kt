package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.ViewModel.CommandeProduitsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.ClientsMapFilterViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepositoryImpl
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.ViewModel.ViewModelT2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.ViewModel.PanierFinaleDAchatViewModel
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.VendeursViewModel
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID1.VentHistoriques.Fragment.ViewModel.PeriodeVenteViewModel
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ViewModel.TariffsButtonsViewModelSec7ID2
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.W.Test.B_ClientInfosProtoJuin3PreviewViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Z.Preview.D_EtateMessageVocalePreviewViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto._1_5_Vendeur._1_5_VendeurRepositoryImpl
import Z_CodePartageEntreApps.DataBase.Juin3.Proto._1_5_Vendeur._1_5_Vendeur_Repository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Preview.A_ProduitInfosViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Preview.CategoriePrevViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepositoryImpl
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_RepositoryImpl
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepositoryImpl
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
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.SoldArticlesTabelleRepository
import Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle.Repository.SoldArticlesTabelleRepositoryImpl
import Z_CodePartageEntreApps.Modules.A_FirebaseAudioStorageHelper
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.RecordingHandler
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.ConnectionManager
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.Glide.CalculeCouleurHandler
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase.F0_FireBaseOperationsHandler
import Z_CodePartageEntreApps.Proto.Par.Type.Modules.SQL.G_RoomOperationsHandler
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.E_GroupedDataBasesRepositoryNonConnue
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Impl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3TransactionCommercialRepository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial_RepositoryImp
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_Repository
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_RepositoryImpl
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_RepositoryImpl
import Z_CodePartageEntreApps.Windows.B.Windows.ViewModel.ViewModelFragment_StartUpScreen
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val itsProductionMode = false

val commonRepositoriesModule = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }


    single {
        E_GroupedDataBasesRepositoryNonConnue(
            get(),
            get(),
            get(),
            get(),
        )
    }

    single {
        F0_FireBaseOperationsHandler()
    }

    single {
        G_RoomOperationsHandler(
            get(),
        )
    }
    single<_01_VentsHistoriquesDataBase_Repository> {
        _01_VentsHistoriquesDataBase_RepositoryImpl(itsProductionMode)
    }

    single<GroupeRepositorysProtoAvJuin3> {
        GroupeRepositorysProtoAvJuin3Impl(
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
    single<C3TransactionCommercialRepository> { C3_TransactionCommercial_RepositoryImp(get()) }
    single<_1_4_PeriodeVent_Repository> { _1_4_PeriodeVentRepositoryImpl(get()) }
    single<_1_5_Vendeur_Repository> { _1_5_VendeurRepositoryImpl(get()) }

    single<_2_1_ProduitsDataBase_Repository> { _2_1_ProduitsDataBase_RepositoryImpl(get()) }

    single<_4_CouleurOperationCommand_Repository> {
        _4_CouleurOperationCommand_RepositoryImpl(get())
    }

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

val classesHandlersModule = module {
    single {
        A_FirebaseAudioStorageHelper(
            )
    }
    single {
        AudioRecorderAndPlayHandler(
            get(),
            )
    }
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    // Recording Handler
    single<IRecordingHandler> {
        RecordingHandler(
            repository = get<K_TempTravailleRepository>(),
            coroutineScope = get<CoroutineScope>()
        )
    }

    // Add other handlers here as needed
    // single<IAnotherHandler> { AnotherHandlerImpl(get()) }
}

val viewModelModule = module {
    viewModel {
        PanierFinaleDAchatViewModel(
            get(),
            get(),
            get(),
        )
    }
    viewModel {
        VendeurAfficheurInfosProduitViewModel(
            get(),
        )
    }
    viewModel {
        B_ClientInfosProtoJuin3PreviewViewModel(
            get(),
        )
    }
    viewModel {
        ViewModelMessageur(
            get(),
            get(),
        )
    }
    viewModel {
        D_EtateMessageVocalePreviewViewModel(
            get(),
        )
    }

    viewModel {
        A_ProduitInfosViewModel(
            get()
        )
    }

    viewModel {
        CategoriePrevViewModel(
            get()
        )
    }

    viewModel {
        EditeBaseDonneMainScreenIdS9ViewModel(
            get(),
        )
    }
    viewModel {
        CommandeProduitsViewModel(
            get(),
        )
    }
    viewModel {
        ViewModelT2(
            get(),
        )
    }


    viewModel { TariffsButtonsViewModelSec7ID2(get(),
            get(),
            get(),) }

    factory { (viewModel: HeadViewModel, context: Context) ->
        ConnectionManager(
            context = context,
        )
    }

    viewModel { (context: Context) ->
        HeadViewModel(
            get(),
            AppDatabase.DatabaseModule.getDatabase(get()),
            get(),
        )
    }

    // Updated to inject the RecordingHandler
    viewModel {
        RecordingViewModel(
            get(),
            get(),
            get(),
            get(),
        )
    }

    viewModel { PeriodeVenteViewModel(get()) }
    viewModel { ViewModelFragment_StartUpScreen(get(), get(), get(), get(), get()) }
    viewModel { ViewModelInitApp(get(), get(), get(), get(), get(),
        get(),
        ) }
    viewModel { VendeursViewModel(get(), get()) }
    viewModel {
        MapClientsViewModel(
            get(),
            get(),
            get(),
            get(),
        )
    }

    // Update this ViewModel to use the navigation handler
    viewModel {
        E0AfficheHistoriqueTransactionsViewModel(
            get(),
            get() ,
            get() ,
            get() ,
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
    single { CalculeCouleurHandler(get()) } // Injects ViewModel_TestID2

    single { PanelsGroupeButtonHandler() }
}

// Updated appModule to include classesHandlersModule
val appModule = module {
    includes(
        moduleGrouperKoinProtoJuin3,
        commonRepositoriesModule,
        appTypeModule,
        viewModelModule,
        navigationModule,
        uiHandlersModule,
        classesHandlersModule  // Added the new module
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
