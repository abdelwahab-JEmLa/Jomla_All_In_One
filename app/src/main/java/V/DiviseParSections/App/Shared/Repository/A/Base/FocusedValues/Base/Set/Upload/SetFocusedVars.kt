package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.add_New_M8BonVent
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.focuceOnVentM3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.focucePourPrixDeM1Produit
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.set_null_CurrentApp_focusedAuPrixDifineurM1ProduitInfosKeyId
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
    fun upsert_M8BonVent_Et_Focuce_Le_Au_M9CurrCompt(
        updatedDefaultId8BonVent: M8BonVent,
        newCurrentM9AppCompt: Z_AppCompt?
    ) {
        add_New_M8BonVentFacade(updatedDefaultId8BonVent)

        if (newCurrentM9AppCompt != null) {
            updateFocuceM9AppCompt(newCurrentM9AppCompt)
        }
    }

    fun add_New_M8BonVentFacade(id8BonVent: M8BonVent) = add_New_M8BonVent(id8BonVent, repo8BonVent)

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

    fun anulle_Focuce_Pour_PrixDeM1ProduitFacade() {
        set_null_CurrentApp_focusedAuPrixDifineurM1ProduitInfosKeyId(get, repo9AppCompt)
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


    fun desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent(): Unit {
        get.currentM9AppCompt?.let {
            repo9AppCompt.upsert(
                it.copy(
                    onVentM8BonVentKey = "null",
                    onVentM8BonVentDebugInfos = "null",
                )
            )
        }
    }

    fun active_CurrentApp_activeDialogSearchM1Produit(value: Boolean) {
        set_CurrentApp_activeDialogSearchM1Produit(value)
    }

    fun dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit() {
        set_CurrentApp_activeDialogSearchM1Produit(false)
        set_Current_startTextSearchM1Produit("")
        anulle_Focuce_Pour_PrixDeM1ProduitFacade()
    }

    private fun set_CurrentApp_activeDialogSearchM1Produit(value: Boolean) {
        repo9AppCompt.currentAppCompt.let {
            if (it != null) {
                repo9AppCompt.upsert(
                    it.copy(
                        activeDialogSearchM1Produit = value
                    )
                )
            }
        }
    }

    fun set_Current_startTextSearchM1Produit(nom: String) {
        repo9AppCompt.currentAppCompt.let {
            if (it != null) {
                repo9AppCompt.upsert(
                    it.copy(
                        startTextSearchM1Produit = nom
                    )
                )
            }
        }
    }
}
