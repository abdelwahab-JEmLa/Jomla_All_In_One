package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.BSetterFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.ModulesCentral
import V.DiviseParSections.App.Shared.Repository.B1CouleurOuGoutProduitDataBaseRepository
import V.DiviseParSections.App.Shared.Repository.BProduitInfosRepository
import V.DiviseParSections.App.Shared.Repository.CCategoriesCompoRepository
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FVentCouleurOperationRepository
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.VentOperations
import V.DiviseParSections.App.Shared.Repository.MID2ClientRepository.Repository.HClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVentRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository.KAchatCouleurOperationRepository
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriodeRepository
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.Z_AutreStatesCompoRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val composRepositorysModule = module {
    single { ZAppCompt_RepositoryComposable(get()) }

    single { HClientRepository(get(), get(), get(), ) }
    single { CCategoriesCompoRepository(get()) }
    single { Z_AutreStatesCompoRepository(get()) }

    single { A_GroupeValuesA_ProduitsToB_Categories(get(), get()) }

    single { BProduitInfosRepository(get()) }
    single { B1CouleurOuGoutProduitDataBaseRepository(get()) }
    single { FVentCouleurOperationRepository(get(), get()) }
    single { GBonVentRepository(get(), get()) }
    single { KAchatCouleurOperationRepository(get()) }
    single { MVentPeriodeRepository(get(), get(), get()) }

    // Helper classes for BSetterFacade
    single { BonVentOperations(get(), get(), get()) }
    single { ClientOperations(get(), get()) }
    single { ProduitOperations(get()) }
    single { VentOperations(get(), get()) }

    single {
        AGetter(
            context = androidContext(),
            get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()
        )
    }

    single {
        BSetterFacade(
            get(), get(), get(), get(),get(),
        )
    }

    single { ModulesCentral(get(), get()) }
    single { ACentralFacade(get(), get(), get()) }
}
