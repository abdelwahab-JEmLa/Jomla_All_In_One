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
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository.E_AchatOperationComposeRepositoryPJ17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_AppCompt.Repository.Z_AppComptComposeRepositoryProtoJuin17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A2_Passive.B_ClientsStateCompoRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A2_Passive.D_TransactionCommercialCompoRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A2_Passive.Z_AutreStatesCompoRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_CentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.C_CategoriesCompoRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ViewModel.TariffsButtonsViewModelSec7ID2
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin17.Proto.D_AchatOperationRepository.Base.D_AchatOperationDataBasePJ17
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.W.Test.B_ClientInfosProtoJuin3PreviewViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Z.Preview.D_EtateMessageVocalePreviewViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Preview.A_ProduitInfosViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Preview.CategoriePrevViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepositoryImpl
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.AvantJuin3._1_5_Vendeur.Proto._1_5_VendeurRepositoryImpl
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.AvantJuin3._1_5_Vendeur.Proto._1_5_Vendeur_Repository
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Z_AppComptRepositoryProtoJuin17
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
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
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
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val centralDataBasesModule = module {
    single { A_MasterRepositorysGrpProtoJuin3(get(), get(), get(), get(), get()) }
    single { E_GroupedDataBasesRepositoryNonConnue(get(), get(), get(), get()) }
    single<GroupeRepositorysProtoAvJuin3> { GroupeRepositorysProtoAvJuin3Impl(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}

val composRepositorysModule = module {
    // Fix: Explicitly specify all 9 dependencies for A_CentralCompoRepositoryProtoJuin9
    single {
        A_CentralCompoRepositoryProtoJuin9(
            context = androidContext(),
            databaseInitializationManager = get<Z_DatabaseInitializationManager>(),
            comptAppState = get<Z_ComptAppStateCompoRepositoryProtoAvanJuin17>(),
            appComptComposeRepositoryProtoJuin17 = get<Z_AppComptComposeRepositoryProtoJuin17>(),
            a_MasterRepositorysGrpProtoJuin3 = get<A_MasterRepositorysGrpProtoJuin3>(),
            b3CategoriesCompoRepository = get<C_CategoriesCompoRepository>(),
            clientsState = get<B_ClientsStateCompoRepository>(),
            transactionCommercialState = get<D_TransactionCommercialCompoRepository>(),
            d_AchatOperationComposeRepositoryPJ17 = get<E_AchatOperationComposeRepositoryPJ17>()
        )
    }

    single { B_ClientsStateCompoRepository(get()) }
    single { C_CategoriesCompoRepository(get()) }
    single { D_TransactionCommercialCompoRepository(get()) }
    single { E_AchatOperationComposeRepositoryPJ17(get()) }
    single { Z_ComptAppStateCompoRepositoryProtoAvanJuin17(get()) }
    single { Z_AppComptComposeRepositoryProtoJuin17(get()) }
    single { Z_AutreStatesCompoRepository(get()) }
}

val viewModelModule = module {
    viewModel { ViewModel_A4FragID1(get(), get()) }
    viewModel { PanierFinaleDAchatViewModel(get(), get(), get()) }
    viewModel { VendeurAfficheurInfosProduitViewModel(get()) }
    viewModel { B_ClientInfosProtoJuin3PreviewViewModel(get()) }
    viewModel { ViewModelMessageur(get(), get()) }
    viewModel { D_EtateMessageVocalePreviewViewModel(get()) }
    viewModel { A_ProduitInfosViewModel(get()) }
    viewModel { CategoriePrevViewModel(get()) }
    viewModel { EditeBaseDonneMainScreenIdS9ViewModel(get(), get()) }
    viewModel { CommandeProduitsViewModel(get()) }
    viewModel { ViewModelT2(get()) }
    viewModel { TariffsButtonsViewModelSec7ID2(get(), get(), get()) }
    factory { (viewModel: HeadViewModel, context: Context) -> ConnectionManager(context = context) }
    viewModel { (context: Context) -> HeadViewModel(get(), AppDatabase.DatabaseModule.getDatabase(get()), get()) }
    viewModel { RecordingViewModel(get(), get(), get(), get()) }
    viewModel { PeriodeVenteViewModel(get()) }
    viewModel { ViewModelFragment_StartUpScreen(get(), get(), get(), get(), get()) }
    viewModel { ViewModelInitApp(get(), get(), get(), get(), get(), get()) }
    viewModel { VendeursViewModel(get(), get()) }
    viewModel { MapClientsViewModel(get(), get(), get(), get()) }
    viewModel { E0AfficheHistoriqueTransactionsViewModel(get(), get(), get(), get()) }
    viewModel { ClientsMapFilterViewModel(get()) }
}

val classesHandlersModule = module {
    single { CalculeCouleurHandler(get()) }
    single { PanelsGroupeButtonHandler() }
    single { FragmentNavigationHandler() }
    single { G_RoomOperationsHandler(get()) }
    single { F0_FireBaseOperationsHandler() }
    single { A_FirebaseAudioStorageHelper() }
    single { AudioRecorderAndPlayHandler(get()) }
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single<IRecordingHandler> { RecordingHandler(repository = get<K_TempTravailleRepository>(), coroutineScope = get<CoroutineScope>()) }
}

val dataBaseProtoAvantJuin3Module = module {
    single<_01_VentsHistoriquesDataBase_Repository> { _01_VentsHistoriquesDataBase_RepositoryImpl(false) }
    single<_1_1_CouleurAcheteOperation_Repository> { _1_1_CouleurAcheteOperationRepositoryImpl(get()) }
    single<_1_2_ProduitAcheteOperation_Repository> { _1_2_ProduitAcheteOperationRepositoryImpl(get()) }
    single<_1_4_PeriodeVent_Repository> { _1_4_PeriodeVentRepositoryImpl(get()) }
    single<_1_5_Vendeur_Repository> { _1_5_VendeurRepositoryImpl(get()) }
    single<_2_1_ProduitsDataBase_Repository> { _2_1_ProduitsDataBase_RepositoryImpl(get()) }
    single<_4_CouleurOperationCommand_Repository> { _4_CouleurOperationCommand_RepositoryImpl(get()) }
    single { A_ProduitInfosRepository(androidContext(), get()) }
    single<A_ProduitRepository> { A_ProduitRepositoryImpl(get()) }
    single { B_ClientInfosProtoJuin3Repository(androidContext(), get()) }
    single { C_CategorieProduitInfosRepository(androidContext(), get()) }
    single<C_GrossistsDataBaseRepository> { C_GrossistsDataBaseRepositoryImpl() }
    single<C3TransactionCommercialRepository> { C3_TransactionCommercial_RepositoryImp(get()) }
    single { D_EtateMessageVocaleRepository(androidContext(), get()) }
    // Moved D_AchatOperationDataBasePJ17 to classesHandlersModule to fix dependency order
    single<E1SecteurDeClientsRepository> { E1SecteurDeClientsRepositoryImpl(get()) }
    single<H_GroupesCategoriesRepository> { H_GroupesCategoriesRepositoryImpl() }
    single<I_CategoriesRepository> { CategoriesRepositoryImpl() }
    single<I_CategorieProduitsRepository> { I_CategorieProduitsRepositoryImpl(get()) }
    single<J_AppInstalleDonTelephoneRepository> { J_AppInstalleDonTelephoneRepositoryImpl() }
    single<K_TempTravailleRepository> { K_TempTravailleRepositoryImpl() }
    single<SoldArticlesTabelleRepository> { SoldArticlesTabelleRepositoryImpl() }
    single { Z_AppComptRepositoryProtoJuin17(get<AppDatabase>().Z_AppComptDao()) }
}

// Alternative approach: Create a separate database module to ensure proper dependency resolution
val databaseModule = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
    single { D_AchatOperationDataBasePJ17(get<AppDatabase>().D_AchatOperationDao()) }
}

val managersModule = module {
    single {
        Z_DatabaseInitializationManager(
            achatOperationRepository = get<D_AchatOperationDataBasePJ17>(),
            appComptComposeRepositoryPJ17 = get<Z_AppComptComposeRepositoryProtoJuin17>()
        )
    }
}

// Updated appModule with proper dependency order and error handling
val appModule = module {
    includes(
        databaseModule,                // 1. Database and DAOs first
        dataBaseProtoAvantJuin3Module, // 2. Basic repositories
        centralDataBasesModule,        // 3. Master repositories
        composRepositorysModule,   // 4. Basic compose repositories
        managersModule,                // 6. Managers (Z_DatabaseInitializationManager)
        classesHandlersModule,         // 8. Handlers and utilities
        viewModelModule                // 9. ViewModels last
    )
}

// Debug module to help identify which dependency is failing
val debugModule = module {
    single {
        try {
            println("Creating Z_AppComptComposeRepositoryProtoJuin17...")
            Z_AppComptComposeRepositoryProtoJuin17(get()).also {
                println("Successfully created Z_AppComptComposeRepositoryProtoJuin17")
            }
        } catch (e: Exception) {
            println("Failed to create Z_AppComptComposeRepositoryProtoJuin17: ${e.message}")
            throw e
        }
    }
}
