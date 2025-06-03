package com.example.serveure.Manager.Koin

import A.AtelierMobile.Test.ID1.Test.Archive.A_.RepositorysPreviewViewModel
import A.AtelierMobile.Test.ID1.Test.Archive.Repository.E_DBJetPackExport.AncienProtoProduitInfosRepository
import A.AtelierMobile.Test.ID1.Test.EditeBaseDonneMainScreen.Fragment.ViewModel.StartUpFragmentViewModel
import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.A_MasterRepositorys
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.A_ProduitInfos.Repository.Preview.A_ProduitInfosViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.C_CategorieProduitInfos.Repository.Preview.CategoriePrevViewModel
import A.AtelierMobile.Test.ID1.Test.Shared.Modules.Glide.CalculeCouleurHandler
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val moduleRepositorys = module {
    single {
        A_ProduitInfosRepository(
            androidContext(),
            get()
        )
    }

    single {
        C_CategorieProduitInfosRepository(
            androidContext(),
            get(),
        )
    }

    single {
        A_MasterRepositorys(
            get(),
            get(),
        )
    }

    single {
        AncienProtoProduitInfosRepository(
            get(),
            androidContext()
        )
    }
}

val moduleViewModels = module {
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
        RepositorysPreviewViewModel(
            get()
        )
    }
    viewModel {
        StartUpFragmentViewModel(
            get(),
        )
    }
}

val moduleHandlersClasses = module {
    single { CalculeCouleurHandler(get()) }
}

val moduleGrouperKoinProtoJuin3 = module {
    includes(
        moduleRepositorys,
        moduleViewModels,
        moduleHandlersClasses
    )
}
