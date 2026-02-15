package com.example.clientjetpack.App2.App.A.Main.App

import org.koin.dsl.module

val composRepositorysModule_app2 = module {
    /* single { Repo18CentralParametresOfAllApps(get()) }

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
     single { A_ProduitDataBaseProtoJuin17(get<AppDatabase>().dao_M1Produit()) }
     single<A_ProduitRepository> { A_ProduitRepositoryImpl(get()) }
     single<_2_1_ProduitsDataBase_Repository> { _2_1_ProduitsDataBase_RepositoryImpl(get()) }

     single { C_CategorieProduitInfosRepository(androidContext(), get()) }
     single { Repo03CouleurProduitInfos(get()) }
     single { DataBaseInitFactory_M3CouleurProduitInfos(get<AppDatabase>().dao_M3CouleurProduitInfos()) }

     single { Repo10OperationVentCouleur(context = androidContext(), get(), get()) }

     single { Repo8BonVent(androidContext(), get(), get()) }
     single<DataBaseInitFactory_8BonVent> { DataBaseInitFactory_8BonVent(get()) }

     // Repos proto ancien (dépendances transitives de A_MasterRepositorysGrpProtoJuin3)
     single<_01_VentsHistoriquesDataBase_Repository> { _01_VentsHistoriquesDataBase_RepositoryImpl(false) }
     single<_1_1_CouleurAcheteOperation_Repository> { _1_1_CouleurAcheteOperationRepositoryImpl(get()) }
     single<_1_2_ProduitAcheteOperation_Repository> { _1_2_ProduitAcheteOperationRepositoryImpl(get()) }
     single<_4_CouleurOperationCommand_Repository> { _4_CouleurOperationCommand_RepositoryImpl(get()) }
     single<DataBaseFactoryMVentPeriode> { _DataBaseFactory_MVentPeriodeImpl(get()) }

     // Beans du centralDataBasesModule (ancienne archi) — requis par Repo2Client
     single { E_GroupedDataBasesRepositoryNonConnue(get(), get(), get(), get()) }
     single { A_MasterRepositorysGrpProtoJuin3(get(), get(), get(), get(), get()) }
     single<GroupeRepositorysProtoAvJuin3> {
         GroupeRepositorysProtoAvJuin3Impl(get(), get(), get(), get(), get(), get(), get())
     }                         */

    /*   single {
           WDatabaseInitializationManager_app2(
               androidContext(),
               get(),
               get(),
               get(),
               get(),
               get(),
           )
       }     */
}
