package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.BSetterFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedVarsHandlerFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.GetterFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.ModulesCentral
import V.DiviseParSections.App.Shared.Repository.A.Base.SetterFocusedVars
import V.DiviseParSections.App.Shared.Repository.CCategoriesCompoRepository
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.VentOperations
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository.KAchatCouleurOperationRepository
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriodeRepository
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.Z_AutreStatesCompoRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val composRepositorysModule = module {
    single { Repo13TarificationInfos(get(),get(),) }
    single { Repo9AppCompt(get()) }

    single { Repo2Client(get(), get(), get(),get(), ) }
    single { CCategoriesCompoRepository(get()) }
    single { Z_AutreStatesCompoRepository(get()) }

    single { A_GroupeValuesA_ProduitsToB_Categories(get(), get()) }

    single { RepoM1ProduitInfos(get()) }
    single { Repo3CouleurProduitInfos(get()) }
    single { Repo10OperationVentCouleur(get(), get()) }
    single { Repo8BonVent(get(), get()) }
    single { KAchatCouleurOperationRepository(get()) }
    single { MVentPeriodeRepository(get(), get(), get()) }

    // Helper classes for BSetterFacade
    single { BonVentOperations(get(), get(), get()) }
    single { ClientOperations(get(), get()) }
    single { ProduitOperations(get()) }
    single { VentOperations(get(), get()) }


    single { GetterFocusedVars(get(), get(),get(),get(),get(),get(),) }
    single { SetterFocusedVars(get(), get(), get(), get(), get(), ) }
    single { FocusedVarsHandlerFacade(get(), get()) }

    single { AGetter(context = androidContext(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get() ,get(),get(),) }

    single { BSetterFacade(get(), get(), get(), get(),get(),get(),get(),get(), get(), ) }
    single { ModulesCentral(get(), get(), get(),) }
    single { ACentralFacade(get(), get(), get(), get()) }
}
