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
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseInitFactory_M3CouleurProduitInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.DataBaseCreationFactory13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory.DataBaseInitFactory_2ClientProtoJuil28
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory.DataBaseInitFactory_14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory.DataBaseInitFactory_16CategorieProduit
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.DataBaseInitFactory_8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.DataBaseInit_Z_AppCompt
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
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
    single { RepositorysMainSetter_app2(get(),
        get(),
        get(),
        get(),
        get(), get(),
        get(),
        get(),
        get(),
        get()
    ) }
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
    single { ACentralFacade_app2(get()
    ) }
}

val classesHandlersModule_app2 = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
    viewModel { WifiConexiontLuncher(androidContext(),get(),get(), ) }
    viewModel { WifiTransferDatas_app2(androidContext(), get(), get(),) }
}

val composRepositorysModule_app2 = module {
    single { Repo18CentralParametresOfAllApps(get(),) }

    single { Repo14VentPeriode(get(),get(),) }
    single { DataBaseInitFactory_14VentPeriode(get()) }

    single { Repo13TarificationInfos(get(),get(),) }
    single { DataBaseCreationFactory13TarificationInfos(get()) }

    single { Repo9AppCompt(context = androidContext(),get(),get(),) }
    single { DataBaseInit_Z_AppCompt(get<AppDatabase>().Z_AppComptDao()) }

    single { Repo2Client(get(), get(), get(), get(), ) }
    single { DataBaseInitFactory_2ClientProtoJuil28(get()) }

    single { RepoM16CategorieProduit(context = androidContext(), get(), ) }
    single { DataBaseInitFactory_16CategorieProduit(get()) }

    single { RepoM1Produit(androidContext(),get(),get(),) }
    single { A_ProduitInfosRepository(androidContext(), get(),) }

    single { Repo03CouleurProduitInfos(get()) }
    single { DataBaseInitFactory_M3CouleurProduitInfos(get<AppDatabase>().B1CouleurOuGoutProduitDataBaseDao()) }

    single { Repo10OperationVentCouleur(context = androidContext(),get(), get()) }

    single { Repo8BonVent( androidContext(),get(), get()) }
    single<DataBaseInitFactory_8BonVent> { DataBaseInitFactory_8BonVent(get()) }


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
        centralDataBasesModule_app2,
        composRepositorysModule_app2,
        classesHandlersModule_app2,

        centralDataBasesModule,
        composRepositorysModule, // This will now be resolved
        factoryDataBaseProtoAvantJuin3Module,
        classesHandlersModule,
        viewModelModule
    )
}
