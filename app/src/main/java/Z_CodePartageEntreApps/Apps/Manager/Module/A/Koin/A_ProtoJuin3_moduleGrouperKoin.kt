package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Preview.A_ProduitInfosViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Preview.CategoriePrevViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_MasterRepositorys
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.Modules.Glide.CalculeCouleurHandler
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
