package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A1.Proto.Juin17.Proto.Z.Repository.Juin9.Proto.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.B_ClientsStateCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.CCategoriesCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.D_TransactionCommercialCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.Z_AutreStatesCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.BProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.DCouleurAchatOperationRepositoryComposable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.DCouleurAchatOperation_SubClassFunctionality
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val composRepositorysModule = module {
    single {
        lazy {
            DCouleurAchatOperation_SubClassFunctionality(get<ACentralCompoRepositoryProtoJuin9>())
        }
    }

    single { ACentralCompoRepositoryProtoJuin9(androidContext(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    single { A_GroupeValuesA_ProduitsToB_Categories(get(), get()) }

    single { BProduitDataBaseComposeRepositoryPJ17(get(),) }

    single { B_ClientsStateCompoRepository(get()) }

    single { CCategoriesCompoRepository(get()) }
    single { D_TransactionCommercialCompoRepository(get()) }

    single { DCouleurAchatOperationRepositoryComposable(get()) }

    single { Z_ComptAppStateCompoRepositoryProtoAvanJuin17(get()) }

    single { ZAppCompt_RepositoryComposable(get()) }

    single { Z_AutreStatesCompoRepository(get()) }
}
