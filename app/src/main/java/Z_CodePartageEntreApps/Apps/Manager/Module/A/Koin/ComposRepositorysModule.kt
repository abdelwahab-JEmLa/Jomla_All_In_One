package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A1.Proto.Juin17.Proto.Z.Repository.Juin9.Proto.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.B_ClientsStateCompoRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.CCategoriesCompoRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.ETransactionCommercialCompoRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.FAchatOperationCouleurRepositoryComposable
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.ZAppCompt_RepositoryComposable
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.Z_AutreStatesCompoRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ASetterCentral
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.B1CouleurOuGoutProduitDataBaseRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.BProduitDataBaseComposeRepositoryPJ17
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val composRepositorysModule = module {
    single { ZAppCompt_RepositoryComposable( get(),) }

    single { B_ClientsStateCompoRepository(get()) }
    single { CCategoriesCompoRepository(get()) }
    single { Z_ComptAppStateCompoRepositoryProtoAvanJuin17(get()) }
    single { Z_AutreStatesCompoRepository(get()) }

    single { A_GroupeValuesA_ProduitsToB_Categories(get(), get()) }

    single { BProduitDataBaseComposeRepositoryPJ17(get()) }
    single { FAchatOperationCouleurRepositoryComposable(get()) }
    single { ETransactionCommercialCompoRepository(get(),get(),) }
    single { B1CouleurOuGoutProduitDataBaseRepository(get(),) }

    single { ACentralCompoRepositoryProtoJuin9(context = androidContext(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),) }
    single { ASetterCentral(get(),get(),get(),) }

}
