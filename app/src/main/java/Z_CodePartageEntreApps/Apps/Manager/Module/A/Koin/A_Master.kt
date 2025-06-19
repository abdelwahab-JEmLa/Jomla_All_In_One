package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.Sec8FWinID1ViewModel
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
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository.E_AchatOperationComposeRepositoryProtoJuin17
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A2_Passive.B_ClientsStateCompoRepository
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A2_Passive.D_TransactionCommercialCompoRepository
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A2_Passive.Z_AutreStatesCompoRepository
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_CentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_ProduitDataBase.Repository.A_ProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.C_CategoriesCompoRepository
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.Z_AppCompt.Repository.Juin17.Proto.Z_AppComptComposeRepositoryProtoJuin17
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.Z_AppCompt.Repository.Juin9.Proto.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Sec10Frag1ViewModel
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID1.VentHistoriques.Fragment.ViewModel.PeriodeVenteViewModel
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ViewModel.TariffsButtonsViewModelSec7ID2
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.A_ProduitDataBase.Base.Juin17.Proto.A_ProduitDataBaseProtoJuin17
import Z_CodePartageEntreApps.DataBase.Juin17.Proto.D_AchatOperationRepository.Base.D_AchatOperationDataBaseProtoJuin17
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
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.Glide.CalculeCouleurHandler
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
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
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val centralDataBasesModule = module {
    single { E_GroupedDataBasesRepositoryNonConnue(get(), get(), get(), get()) }
    single { A_MasterRepositorysGrpProtoJuin3(get(), get(), get(), get(), get()) }
    single<GroupeRepositorysProtoAvJuin3> { GroupeRepositorysProtoAvJuin3Impl(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}

val composRepositorysModule = module {
    single { A_CentralCompoRepositoryProtoJuin9(androidContext(), get(), get(), get(), get(), get(), get(), get(), get(), get(),get()) }
    single { A_GroupeValuesA_ProduitsToB_Categories(get(),get(),) }

    single { A_ProduitDataBaseComposeRepositoryPJ17(get(),get(),) }
    single { B_ClientsStateCompoRepository(get()) }
    single { C_CategoriesCompoRepository(get()) }
    single { D_TransactionCommercialCompoRepository(get()) }
    single { E_AchatOperationComposeRepositoryProtoJuin17(get()) }

    single { Z_ComptAppStateCompoRepositoryProtoAvanJuin17(get()) }
    single { Z_AppComptComposeRepositoryProtoJuin17(get()) }

    single { Z_AutreStatesCompoRepository(get()) }
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
    single { A_ProduitDataBaseProtoJuin17(get<AppDatabase>().ArticlesBasesStatsModelDao()) }

    single { B_ClientInfosProtoJuin3Repository(androidContext(), get()) }
    single { C_CategorieProduitInfosRepository(androidContext(), get()) }
    single<C_GrossistsDataBaseRepository> { C_GrossistsDataBaseRepositoryImpl() }
    single<C3TransactionCommercialRepository> { C3_TransactionCommercial_RepositoryImp(get()) }
    single { D_EtateMessageVocaleRepository(androidContext(), get()) }
    single { D_AchatOperationDataBaseProtoJuin17(get<AppDatabase>().D_AchatOperationDao()) }
    single<E1SecteurDeClientsRepository> { E1SecteurDeClientsRepositoryImpl(get()) }
    single<H_GroupesCategoriesRepository> { H_GroupesCategoriesRepositoryImpl() }
    single<I_CategoriesRepository> { CategoriesRepositoryImpl() }
    single<I_CategorieProduitsRepository> { I_CategorieProduitsRepositoryImpl(get()) }
    single<J_AppInstalleDonTelephoneRepository> { J_AppInstalleDonTelephoneRepositoryImpl() }
    single<K_TempTravailleRepository> { K_TempTravailleRepositoryImpl() }
    single<SoldArticlesTabelleRepository> { SoldArticlesTabelleRepositoryImpl() }
    single { Z_AppComptRepositoryProtoJuin17(get<AppDatabase>().Z_AppComptDao()) }
}

val classesHandlersModule = module {
    single { WifiTransferDatas(androidContext(),) }
    single { Z_DatabaseInitializationManager(get(),get(),) }
    single { CalculeCouleurHandler(get()) }
    single { PanelsGroupeButtonHandler() }
    single { FragmentNavigationHandler() }
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
    single { G_RoomOperationsHandler(get()) }
    single { F0_FireBaseOperationsHandler() }
    single { CalculeCouleurHandler(get()) }
    single { A_FirebaseAudioStorageHelper() }
    single { AudioRecorderAndPlayHandler(get()) }
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single<IRecordingHandler> { RecordingHandler(repository = get<K_TempTravailleRepository>(), coroutineScope = get<CoroutineScope>()) }
}

val viewModelModule = module {
    //Sort Par ID
    viewModel { Sec8FWinID1ViewModel(get(),get(),) }
    viewModel { Sec9FragId1ViewId2ViewModel() }
    viewModel { Sec10Frag1ViewModel(get(),) }

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
    viewModel { RecordingViewModel(get(), get(), get(), get()) }
    viewModel { PeriodeVenteViewModel(get()) }
    viewModel { ViewModelFragment_StartUpScreen(get(), get(), get(), get(), get()) }
    viewModel { ViewModelInitApp(get(), get(), get(), get(), get(), get()) }
    viewModel { VendeursViewModel(get(), get()) }
    viewModel { MapClientsViewModel(get(), get(), get(), get()) }
    viewModel { E0AfficheHistoriqueTransactionsViewModel(get(), get(), get(), get()) }
    viewModel { ClientsMapFilterViewModel(get()) }
    viewModel { HeadViewModel(androidContext(), get(), get()) }
}

val appModule = module {
    includes(
        centralDataBasesModule,
        composRepositorysModule,
        dataBaseProtoAvantJuin3Module,
        viewModelModule,
        classesHandlersModule
    )
}
