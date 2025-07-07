package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.anulleFocucePourPrixDeM1Produit
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.focuceOnVentM3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.focucePourPrixDeM1Produit
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.updateCurrentAppComptDialogProduit
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.runtime.Stable

@Stable
class SetFocusedVars(
    private val get: GetFocusedVars,
    private val Repo2Client: Repo2Client,
    private val repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
) {
    fun addNewM8BonVent(id8BonVent: M8BonVent) =
        ajoutCopyDefaultBonVentEtFocuceLeAuAppCompt(id8BonVent, repo8BonVent)

    fun addNewM2ClientInfos(newClient: HClientInfos) = Repo2Client.addClient(newClient)

    fun ajoute_New_M10OperationVentCouleur(it: M10OperationVentCouleur) {
        repo10OperationVentCouleur.addOrUpdateData(it)
    }

    fun updateFocuceM9AppCompt(data: Z_AppCompt) = repo9AppCompt.upsert(data)

    fun active_M3Couleur_pour_ouvrire_son_Dialog_choixQuantity(
        m10OperationVentCouleur: M10OperationVentCouleur
    ) = focuceOnVentM3CouleurProduitInfos(
        m10OperationVentCouleur = m10OperationVentCouleur,
        getterFocusedVars = get,
        repo9AppCompt = repo9AppCompt,
    )

    fun fermeDialogChoisireQuantityDeVentCouleur(produitKey: String) {
        focuceOnVentM3CouleurProduitInfos(
            getterFocusedVars = get,
            repo9AppCompt = repo9AppCompt,
        )
        focucePourPrixDeM1Produit(
            produitKey,
            get,
            repo9AppCompt
        )
    }

    fun active_M1Produit_Pour_Choisire_TotalQuantity(produit: ArticlesBasesStatsTable) =
        updateCurrentAppComptDialogProduit(
            get,
            repo9AppCompt,
            produit,
        )

    fun focucePourPrixDeM1ProduitFacade(produit: ArticlesBasesStatsTable) {
        focucePourPrixDeM1Produit(
            produit.keyID,
            get,
            repo9AppCompt
        )
    }

    fun fermeFocucePourPrixDeM1ProduitDialogChoisireQuantityFacade(produit: ArticlesBasesStatsTable) {
        updateCurrentAppComptDialogProduit(get, repo9AppCompt)
        focucePourPrixDeM1Produit(
            produit.keyID,
            get,
            repo9AppCompt
        )
    }

    fun anulleFocucePourPrixDeM1ProduitFacade() {
        anulleFocucePourPrixDeM1Produit(get, repo9AppCompt)
    }

    fun active_currentApp_M8BonVent(bonVent: M8BonVent): Unit {
        get.currentM9AppCompt?.let {
            repo9AppCompt.upsert(
                it.copy(
                    onVentM8BonVentKey = bonVent.keyID,
                    onVentM8BonVentDebugInfos = bonVent.debugInfos,
                )
            )
        }
    }

    fun desactive_currentApp_M8BonVent(): Unit {
        get.currentM9AppCompt?.let {
            repo9AppCompt.upsert(
                it.copy(
                    onVentM8BonVentKey = "null",
                    onVentM8BonVentDebugInfos = "null",
                )
            )
        }
    }
}
