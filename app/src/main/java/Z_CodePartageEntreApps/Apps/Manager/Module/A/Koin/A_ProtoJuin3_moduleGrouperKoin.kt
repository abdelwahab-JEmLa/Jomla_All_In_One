package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import Z_CodePartageEntreApps.Repository.Repository.A_CentralDatasHandlerProtoJuin9
import Z_CodePartageEntreApps.Repository.Repository.B3CategoriesCompoRepository
import Z_CodePartageEntreApps.Repository.Repository.B_ClientsState
import Z_CodePartageEntreApps.Repository.Repository.C_TransactionCommercialState
import Z_CodePartageEntreApps.Repository.Repository.D_ComptAppState
import Z_CodePartageEntreApps.Repository.Repository.Z_AutreStates
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.Modules.Glide.CalculeCouleurHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val moduleRepositorys = module {

    single {
        B_ClientInfosProtoJuin3Repository(
            androidContext(),
            get(),
        )
    }
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
            get(),
        )
    }

}

val moduleComposRepositorys = module {
    single {
        B3CategoriesCompoRepository(
            get(),
        )
    }
    single {
        Z_AutreStates(
            get(),
        )
    }

    single {
        B_ClientsState(
            get(),
        )
    }
    single {
        D_ComptAppState(
            get(),
        )
    }
    single {
        C_TransactionCommercialState(
            get(),
        )
    }
    single {
        A_CentralDatasHandlerProtoJuin9(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
}

val moduleHandlersClasses = module {
    single { CalculeCouleurHandler(get()) }
}

val moduleGrouperKoinProtoJuin3 = module {
    includes(
        moduleComposRepositorys,
        moduleRepositorys,
        moduleHandlersClasses
    )
}
