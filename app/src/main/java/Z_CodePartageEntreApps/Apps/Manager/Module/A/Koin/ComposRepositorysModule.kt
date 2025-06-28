package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ACentral
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ASetterCentral
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.BProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FClientRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FVentCouleurOperationRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.GBonVentRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.GrossistAchat.Fragment.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBaseRepository
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.CCategoriesCompoRepository
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.Z_AutreStatesCompoRepository
import Z_CodePartageEntreApps.Repository.Main.Proto.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val composRepositorysModule = module {
    single { ZAppCompt_RepositoryComposable( get(),) }

    single { FClientRepository(get(),get(),) }
    single { CCategoriesCompoRepository(get()) }
    single { Z_ComptAppStateCompoRepositoryProtoAvanJuin17(get()) }
    single { Z_AutreStatesCompoRepository(get()) }

    single { A_GroupeValuesA_ProduitsToB_Categories(get(), get()) }

    single { BProduitDataBaseComposeRepositoryPJ17(get()) }
    single { FVentCouleurOperationRepository(get(),get(),) }
    single { GBonVentRepository(get(),get(),get(),) }
    single { B1CouleurOuGoutProduitDataBaseRepository(get(),) }

    single { ACentralCompoRepositoryProtoJuin9(context = androidContext(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),) }
    single { ASetterCentral(get(),get(),get(), ) }
    single { ACentral( get(), get(),) }
}
