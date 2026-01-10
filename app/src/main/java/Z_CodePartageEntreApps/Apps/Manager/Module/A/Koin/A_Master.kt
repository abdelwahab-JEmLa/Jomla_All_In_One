package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.ClientsMapFilterViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepositoryImpl
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.ViewModel.ViewModelT2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PrintInPdf_itextpdf_Handler
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.UploadHandler
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.D4.ControleApps.App.FragID2.Screen_M9AppCompt.Fragment.ViewModel_M9AppCompt
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID1.VentHistoriques.Fragment.ViewModel.PeriodeVenteViewModel
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.DebugKey
import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.ViewModelDebugFloatingButton
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Test.B_ClientInfosProtoJuin3PreviewViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Repository._01_VentsHistoriquesDataBase_RepositoryImpl
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Z.Preview.D_EtateMessageVocalePreviewViewModel
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.A_ProduitDataBaseProtoJuin17
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.ViewModel_DataBaseInitFactory_1Produit
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseInitFactory_B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.A.Main.B1CouleurOuGoutProduitDataBaseTestDatasViewModel
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.DataBaseCreationFactory13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.Proto.FireBase.F0_FireBaseOperationsHandler
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.Proto.SQL.G_RoomOperationsHandler
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.Preview.D_AchatOperationTestDatasViewModel
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory.DataBaseInitFactory_2ClientProtoJuil28
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase11.Factory.DataBaseInitFactory_11AchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory.DataBaseInitFactory_14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.DataBaseInitFactory_15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Preview.Preview_DataBaseInitFactory_15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory.DataBaseInitFactory_16CategorieProduit
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase19.Factory.DataBaseInitFactory_19Etudiant
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase20.Factory.DataBaseInitFactory_M20ObsarvationEtudion
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.DataBaseInitFactory_8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Z_AppComptRepositoryProtoJuin17
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Preview.A_ProduitInfosViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Preview.CategoriePrevViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository.K_TempTravailleRepositoryImpl
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
import Z_CodePartageEntreApps.Modules.D.Glide.Proto.CalculeCouleurHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.E_GroupedDataBasesRepositoryNonConnue
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Impl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperationRepositoryImpl
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent.DataBaseFactoryMVentPeriode
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._DataBaseFactory_MVentPeriodeImpl
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
    single<GroupeRepositorysProtoAvJuin3> {
        GroupeRepositorysProtoAvJuin3Impl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
}

val factoryDataBaseProtoAvantJuin3Module = module {
    single { DataBaseInitFactory_M20ObsarvationEtudion(get()) }
    single { DataBaseInitFactory_19Etudiant(get()) }
    single { DataBaseInitFactory_2ClientProtoJuil28(get()) }
    single { DataBaseInitFactory_16CategorieProduit(get()) }
    single { DataBaseInitFactory_11AchatOperation(get()) }
    single { DataBaseInitFactory_15Grossist(get()) }
    single { DataBaseInitFactory_14VentPeriode(get()) }

    single<_01_VentsHistoriquesDataBase_Repository> {
        _01_VentsHistoriquesDataBase_RepositoryImpl(
            false
        )
    }
    single<_1_1_CouleurAcheteOperation_Repository> { _1_1_CouleurAcheteOperationRepositoryImpl(get()) }
    single<_1_2_ProduitAcheteOperation_Repository> { _1_2_ProduitAcheteOperationRepositoryImpl(get()) }
    single<DataBaseFactoryMVentPeriode> { _DataBaseFactory_MVentPeriodeImpl(get()) }
    single<_2_1_ProduitsDataBase_Repository> { _2_1_ProduitsDataBase_RepositoryImpl(get()) }
    single<_4_CouleurOperationCommand_Repository> { _4_CouleurOperationCommand_RepositoryImpl(get()) }

    single { A_ProduitInfosRepository(androidContext(), get()) }
    single<A_ProduitRepository> { A_ProduitRepositoryImpl(get()) }
    single { A_ProduitDataBaseProtoJuin17(get<AppDatabase>().ArticlesBasesStatsModelDao()) }
    single { DataBaseInitFactory_B1CouleurOuGoutProduitDataBase(get<AppDatabase>().B1CouleurOuGoutProduitDataBaseDao()) }


    single { DataBaseCreationFactory13TarificationInfos(get()) }
    single { dataBaseCreationFactoryMID2ClientRepository(androidContext(), get()) }
    single { C_CategorieProduitInfosRepository(androidContext(), get()) }
    single<C_GrossistsDataBaseRepository> { C_GrossistsDataBaseRepositoryImpl() }
    single<DataBaseInitFactory_8BonVent> { DataBaseInitFactory_8BonVent(get()) }
    single { Repo17MessageVocale(androidContext(), get()) }
    single { DataBaseFactoryDCouleurAchatOperation(get<AppDatabase>().D_AchatOperationDao()) }
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
    single { UploadHandler() }
    single { PrintInPdf_itextpdf_Handler(get(), get(), ) }
    single { PrintReceiptHandler_Juil( get()) }
    single { DebugKey(get()) }
    single { WifiTransferDatas(androidContext(), get(), get()) }
    single { WDatabaseInitializationManager(get(), get(),get(), get(), get(),get(),get(),get(), get(),get(),get(),get(),get(), ) }

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
    single<IRecordingHandler> {
        RecordingHandler(
            repository = get<K_TempTravailleRepository>(),
            coroutineScope = get<CoroutineScope>()
        )
    }
}

val viewModelModule = module {
    viewModel { ViewModel_M9AppCompt(get(),  ) }
    viewModel { Preview_DataBaseInitFactory_15Grossist(get(),  ) }
    viewModel { ViewModel_DataBaseInitFactory_1Produit(get(),  ) }
    viewModel { ViewModel_M14VentPeriod(get(),  ) }

    viewModel { ViewModelsProduit_T1(get()) }
    viewModel { ViewModelDebugFloatingButton(get()) }
    viewModel { ViewModelMainFastSearchProduitPourVent(get()) }
    viewModel { B1CouleurOuGoutProduitDataBaseTestDatasViewModel(get()) }

    viewModel { PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel(get()) }
    viewModel { D_AchatOperationTestDatasViewModel(get()) }
    viewModel { ZViewModel_Sec1Frag3(get()) }

    viewModel { Sec9FragId1ViewId2ViewModel(get()) }

    viewModel { ViewModel_A4FragID1(get(), get()) }
    viewModel { VendeurAfficheurInfosProduitViewModel(get()) }
    viewModel { B_ClientInfosProtoJuin3PreviewViewModel(get()) }
    viewModel { ViewModelMessageur(get(), get(), get(), get()) }
    viewModel { D_EtateMessageVocalePreviewViewModel(get()) }
    viewModel { A_ProduitInfosViewModel(get()) }
    viewModel { CategoriePrevViewModel(get()) }
    viewModel { EditeBaseDonneMainScreenIdS9ViewModel(get(), get()) }
    viewModel { GrossistAchatSec12FragID1_ViewModel(get()) }
    viewModel { ViewModelT2(get()) }
    viewModel { TariffsButtonsViewModelSec7ID2(get(), get(), get()) }
    viewModel { RecordingViewModel(get(),get(), get(), get(), get()) }
    viewModel { PeriodeVenteViewModel(get()) }
    viewModel { ViewModelFragment_StartUpScreen(get(), get(), get(), get()) }
    viewModel { MapClientsViewModel(get(), get(), get(), get(), get()) }
    viewModel { E0AfficheHistoriqueTransactionsViewModel(get(), get(), get(), get()) }
    viewModel { ClientsMapFilterViewModel(get()) }
    viewModel { ViewModelPresistantButtonsSec8FWinID1(get(), get()) }

    viewModel { ViewModelInitApp(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { HeadViewModel(get(), androidContext(), get(), get()) }

}

val appModule = module {
    includes(
        centralDataBasesModule,
        composRepositorysModule, // This will now be resolved
        factoryDataBaseProtoAvantJuin3Module,
        classesHandlersModule,
        viewModelModule
    )
}
