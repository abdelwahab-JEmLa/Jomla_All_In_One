package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A1.Proto.Juin17.Proto.Z.Repository.Juin9.Proto.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.B_ClientsStateCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.CCategoriesCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.D_TransactionCommercialCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.Z_AutreStatesCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.Z_SubClassFunctionality_ZAppCompt
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.BProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.DCouleurAchatOperationRepositoryComposable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.DCouleurAchatOperation_SubClassFunctionality
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val composRepositorysModule = module {

    // Basic repositories without circular dependencies
    single { A_GroupeValuesA_ProduitsToB_Categories(get(), get()) }
    single { BProduitDataBaseComposeRepositoryPJ17(get()) }
    single { B_ClientsStateCompoRepository(get()) }
    single { CCategoriesCompoRepository(get()) }
    single { D_TransactionCommercialCompoRepository(get()) }
    single { Z_ComptAppStateCompoRepositoryProtoAvanJuin17(get()) }
    single { Z_AutreStatesCompoRepository(get()) }

    // Create DCouleurAchatOperationRepositoryComposable first without SubClassFunctionality
    single {
        DCouleurAchatOperationRepositoryComposable(
            ancienRepo = get(),
            subClassFunctionalityLazy = lazy { get<DCouleurAchatOperation_SubClassFunctionality>() }
        )
    }

    // Create ZAppCompt_RepositoryComposable with its SubClassFunctionality
    single {
        ZAppCompt_RepositoryComposable(
            ancienRepo = get(),
            subClassFunctionalityLazy = lazy { get<Z_SubClassFunctionality_ZAppCompt>() }
        )
    }

    // Create ACentralCompoRepositoryProtoJuin9
    single {
        ACentralCompoRepositoryProtoJuin9(
            context = androidContext(),
            databaseInitializationManager = get(),
            bProduitDataBase_SubClassFunctionality = get(),
            a_GroupeValuesA_ProduitsToB_Categories = get(),
            b3CategoriesCompoRepository = get(),
            clientsState = get(),
            transactionCommercialState = get(),
            dCouleurAchatOperationRepositoryComposable = get(),
            zAppComptRepositoryComposable = get(),
            comptAppState = get(),
            a_MasterRepositorysGrpProtoJuin3 = get()
        )
    }

    // Now create the SubClassFunctionality classes that depend on ACentralCompoRepositoryProtoJuin9
    single {
        DCouleurAchatOperation_SubClassFunctionality(
            centralRepoLazy = lazy { get<ACentralCompoRepositoryProtoJuin9>() }
        )
    }

    single {
        Z_SubClassFunctionality_ZAppCompt(
            centralRepoLazy = lazy { get<ACentralCompoRepositoryProtoJuin9>() }
        )
    }
}
