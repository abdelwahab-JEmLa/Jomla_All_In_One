package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.ModulesCentral
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.Repo15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.RepoM16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.Repo20ObsarvationEtudion
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriodeRepository
import Z_CodePartageEntreApps.DataBase.Repo18CentralParametresOfAllApps
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.Z_AutreStatesCompoRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val composRepositorysModule = module {
    single { Repo20ObsarvationEtudion(context = androidContext(), get(),) }
    single { Repo19Etudiant(context = androidContext(),get(),) }

    single { Repo18CentralParametresOfAllApps(get(),) }

    single { Repo15Grossist(context = androidContext(),get(),) }

    single { Repo14VentPeriode(get(),get(),) }
    single { Repo13TarificationInfos(get(),get(),) }
    single { Repo9AppCompt(context = androidContext(),get(),get(),) }

    single { Repo2Client(get(), get(), get(), get(), ) }
    single { RepoM16CategorieProduit(context = androidContext(), get(), ) }
    single { Z_AutreStatesCompoRepository(get()) }

    single { A_GroupeValuesA_ProduitsToB_Categories(get(), get()) }

    single { RepoM1Produit(androidContext(),get(),get(),) }
    single { Repo03CouleurProduitInfos(get()) }
    single { Repo10OperationVentCouleur(context = androidContext(),get(), get()) }
    single { Repo8BonVent( androidContext(),get(), get()) }
    single { Repo11AchatOperation(androidContext(), get(), get(),) }
    single { MVentPeriodeRepository(get(), get(), get()) }

    // Helper classes for RepositorysMainSetter
    single { ClientOperations(get(), get()) }
    single { ProduitOperations(get()) }


    single { FocusedValuesGetter(get(), get(),get(),get(),get(),get(),get(),get(),get(),) }
    single { FocusedValuesSetter(get(), get(), get(), get(), get(),get(), ) }
    single { FocusedActiveValuesFacade(get(), get()) }

    single { RepositorysMainGetter(
        context = androidContext(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(), get(),get(),get(), ) }

    single { RepositorysMainSetter(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),get(), get(), get(),get(),get(),
        get(),
        get(),get(),
        ) }
    single { ModulesCentral(get(), get(), get(),get(),get(),) }
    single { ACentralFacade(get(), get(), get(), get()) }
}
