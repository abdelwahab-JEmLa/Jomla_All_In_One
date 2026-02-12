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
import com.example.clientjetpack.App2.App.A.Main.Base.Modules.WifiConexiontLuncher
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.ACentralFacade_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainSetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.WDatabaseInitializationManager_app2
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule_app2 = module {
    viewModel { WifiConexiontLuncher(androidContext(),get(),get(), ) }
}
val centralDataBasesModule_app2 = module {

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
            get(),
            get(),
        )
    }

    single { ACentralFacade_app2(get()) }
}

val classesHandlersModule_app2 = module {
}


val composRepositorysModule_app2 = module {
    single { Repo18CentralParametresOfAllApps(get(),) }
    single { Repo14VentPeriode(get(),get(),) }
    single { Repo13TarificationInfos(get(),get(),) }
    single { Repo9AppCompt(context = androidContext(),get(),get(),) }
    single { Repo2Client(get(), get(), get(), get(), ) }
    single { RepoM16CategorieProduit(context = androidContext(), get(), ) }
    single { RepoM1Produit(androidContext(),get(),get(),) }
    single { Repo03CouleurProduitInfos(get()) }
    single { Repo10OperationVentCouleur(context = androidContext(),get(), get()) }
    single { Repo8BonVent( androidContext(),get(), get()) }

    single {
        WDatabaseInitializationManager_app2(
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
        viewModelModule_app2
    )
}
