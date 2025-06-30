package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBaseRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.BProduitInfosRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FVentCouleurOperationRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.KAchatCouleurOperationRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.Shared.Repository.ACentral
import V.DiviseParSections.App.Shared.Repository.AGetter
import V.DiviseParSections.App.Shared.Repository.BSetter
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.VentOperations
import V.DiviseParSections.App.Shared.Repository.GBonVentRepository
import V.DiviseParSections.App.Shared.Repository.HClientRepository
import V.DiviseParSections.App.Shared.Repository.MVentPeriodeRepository
import V.DiviseParSections.App.Shared.Repository.ModulesCentral
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.CCategoriesCompoRepository
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.Z_AutreStatesCompoRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val composRepositorysModule = module {
    single { ZAppCompt_RepositoryComposable(get()) }

    single { HClientRepository(get(), get(), get()) }
    single { CCategoriesCompoRepository(get()) }
    single { Z_AutreStatesCompoRepository(get()) }

    single { A_GroupeValuesA_ProduitsToB_Categories(get(), get()) }

    single { BProduitInfosRepository(get()) }
    single { B1CouleurOuGoutProduitDataBaseRepository(get()) }
    single { FVentCouleurOperationRepository(get(), get()) }
    single { GBonVentRepository(get(), get()) }
    single { KAchatCouleurOperationRepository(get()) }
    single { MVentPeriodeRepository(get(), get(), get()) }

    // Helper classes for BSetter
    single { BonVentOperations(get(), get(), get()) }
    single { ClientOperations(get(),get(),) }
    single { ProduitOperations(get(),) }
    single { VentOperations(get(),get(),) }

    single {
        AGetter(
            context = androidContext(),
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()
        )
    }

    single {
        BSetter(
            get(), get(),get(),get(),)
    }

    single { ModulesCentral(get()) }
    single { ACentral(get(), get(), get()) }
}
