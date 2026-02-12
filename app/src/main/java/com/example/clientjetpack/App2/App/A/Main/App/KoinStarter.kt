package com.example.clientjetpack.App2.App.A.Main.App

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.RepoM16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.centralDataBasesModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.classesHandlersModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.composRepositorysModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.factoryDataBaseProtoAvantJuin3Module
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.viewModelModule
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Repository._01_VentsHistoriquesDataBase_RepositoryImpl
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.A_ProduitDataBaseProtoJuin17
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseInitFactory_M3CouleurProduitInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.DataBaseCreationFactory13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory.DataBaseInitFactory_2ClientProtoJuil28
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory.DataBaseInitFactory_14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory.DataBaseInitFactory_16CategorieProduit
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.DataBaseInitFactory_8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.DataBaseInit_Z_AppCompt
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepositoryImpl
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
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiConexiontLuncher
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiTransferDatas_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.ACentralFacade_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainSetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.WDatabaseInitializationManager_app2
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val centralDataBasesModule_app2 = module {
    single {
        FocusedValuesGetter_app2(
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
    single {
        RepositorysMainSetter_app2(
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
    single {
        RepositorysMainGetter_app2(
            context = androidContext(),
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
    single { ACentralFacade_app2(get()) }
}

val classesHandlersModule_app2 = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
    viewModel { WifiConexiontLuncher(androidContext(), get(), get()) }
    viewModel { WifiTransferDatas_app2(androidContext(), get(), get()) }
}

val composRepositorysModule_app2 = module {
    single { Repo18CentralParametresOfAllApps(get()) }

    single { Repo14VentPeriode(get(), get()) }
    single { DataBaseInitFactory_14VentPeriode(get()) }

    single { Repo13TarificationInfos(get(), get()) }
    single { DataBaseCreationFactory13TarificationInfos(get()) }

    single { Repo9AppCompt(context = androidContext(), get(), get()) }
    single { DataBaseInit_Z_AppCompt(get<AppDatabase>().Z_AppComptDao()) }

    // Dépendances proto ancienne architecture — requises par Repo2Client
    single { dataBaseCreationFactoryMID2ClientRepository(androidContext(), get()) }
    single { Repo2Client(get(), get(), get(), get()) }
    single { DataBaseInitFactory_2ClientProtoJuil28(get()) }

    single { RepoM16CategorieProduit(context = androidContext(), get()) }
    single { DataBaseInitFactory_16CategorieProduit(get()) }

    single { RepoM1Produit(androidContext(), get(), get()) }
    single { A_ProduitInfosRepository(androidContext(), get()) }
    single { A_ProduitDataBaseProtoJuin17(get<AppDatabase>().ArticlesBasesStatsModelDao()) }
    single<A_ProduitRepository> { A_ProduitRepositoryImpl(get()) }
    single { C_CategorieProduitInfosRepository(androidContext(), get()) }

    single { Repo03CouleurProduitInfos(get()) }
    single { DataBaseInitFactory_M3CouleurProduitInfos(get<AppDatabase>().B1CouleurOuGoutProduitDataBaseDao()) }

    single { Repo10OperationVentCouleur(context = androidContext(), get(), get()) }

    single { Repo8BonVent(androidContext(), get(), get()) }
    single<DataBaseInitFactory_8BonVent> { DataBaseInitFactory_8BonVent(get()) }

    // Repos proto ancien (dépendances transitives de A_MasterRepositorysGrpProtoJuin3)
    single<_01_VentsHistoriquesDataBase_Repository> { _01_VentsHistoriquesDataBase_RepositoryImpl(false) }
    single<_1_1_CouleurAcheteOperation_Repository> { _1_1_CouleurAcheteOperationRepositoryImpl(get()) }
    single<_1_2_ProduitAcheteOperation_Repository> { _1_2_ProduitAcheteOperationRepositoryImpl(get()) }
    single<_2_1_ProduitsDataBase_Repository> { _2_1_ProduitsDataBase_RepositoryImpl(get()) }
    single<_4_CouleurOperationCommand_Repository> { _4_CouleurOperationCommand_RepositoryImpl(get()) }
    single<DataBaseFactoryMVentPeriode> { _DataBaseFactory_MVentPeriodeImpl(get()) }

    // Beans du centralDataBasesModule (ancienne archi) — requis par Repo2Client
    single { E_GroupedDataBasesRepositoryNonConnue(get(), get(), get(), get()) }
    single { A_MasterRepositorysGrpProtoJuin3(get(), get(), get(), get(), get()) }
    single<GroupeRepositorysProtoAvJuin3> {
        GroupeRepositorysProtoAvJuin3Impl(get(), get(), get(), get(), get(), get(), get())
    }

    single {
        WDatabaseInitializationManager_app2(
            androidContext(),
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
}

val appModule_App2 = module {
    includes(
        centralDataBasesModule,
        composRepositorysModule,
        factoryDataBaseProtoAvantJuin3Module,
        classesHandlersModule,
        viewModelModule,

        centralDataBasesModule_app2,
        composRepositorysModule_app2,
        classesHandlersModule_app2,
    )
}
