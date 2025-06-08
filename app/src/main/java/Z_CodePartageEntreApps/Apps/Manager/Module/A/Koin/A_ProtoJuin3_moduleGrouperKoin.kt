package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.P.Preview.D_EtateMessageVocalePreviewViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Preview.A_ProduitInfosViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Preview.CategoriePrevViewModel
import Z_CodePartageEntreApps.Modules.Glide.CalculeCouleurHandler
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val moduleRepositorys = module {
    single {
        D_EtateMessageVocaleRepository(
            androidContext(),
            get(),
        )
    }

    single {
        A_ProduitInfosRepository(
            androidContext(),
            get(),
        )
    }

    single {
        C_CategorieProduitInfosRepository(
            androidContext(),
            get(),
        )
    }

    single {
        A_MasterRepositorysGrpProtoJuin3(
            get(),
            get(),
            get(),
            get(),
        )
    }

}

val moduleViewModels = module {
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
