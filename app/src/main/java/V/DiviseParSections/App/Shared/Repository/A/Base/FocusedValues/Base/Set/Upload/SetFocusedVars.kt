package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.focuceOnVentM3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.Functions.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID
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
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import androidx.compose.runtime.Stable

@Stable
class SetFocusedVars(
    private val get: GetFocusedVars,
    private val Repo2Client: Repo2Client,
    private val repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo14VentPeriode: Repo14VentPeriode,
) {
    fun upsert_M8BonVent_Et_Focuce_Le_Au_M9CurrCompt(
        updatedDefaultId8BonVent: M8BonVent,
        newCurrentM9AppCompt: Z_AppCompt?
    ) {
        this.update_M8BonVent(updatedDefaultId8BonVent)

        if (newCurrentM9AppCompt != null) {
            updateFocuceM9AppCompt(newCurrentM9AppCompt)
        }
    }

    fun update_M8BonVent(data: M8BonVent) = repo8BonVent.upsert(data)
    fun add_M8BonVent(defaultM8BonVent: M8BonVent) = repo8BonVent.add(defaultM8BonVent)

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
        setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
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


    fun fermeFocucePourPrixDeM1ProduitDialogChoisireQuantityFacade(produit: ArticlesBasesStatsTable) {
        updateCurrentAppComptDialogProduit(get, repo9AppCompt)
        setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
            produit.keyID,
            get,
            repo9AppCompt
        )
    }

    //--------------------activeFocuce_TariffPrixDifineur_M1ProduitKeyID--------------------------------------------------------
    fun setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(produit: ArticlesBasesStatsTable) {
        setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
            produit.keyID,
            get,
            repo9AppCompt
        )
    }

    fun clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID() {
        clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(get, repo9AppCompt)
    }
    //--------------------activeFocuce_TariffPrixDifineur_M1ProduitKeyID--------------------------------------------------------


    fun active_currentApp_M8BonVent(bonVent: M8BonVent): Unit {
        get.currentM9AppCompt?.let {
            repo9AppCompt.upsert(
                it.copy(
                    onVentM8BonVentKey = bonVent.keyID,
                    onVentM8BonVentDebugInfos = bonVent.get_DebugInfos(),
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
        setIn_CurrentApp_activeDialogSearchM1Produit(value)
    }

    fun dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit() {
        clear_CurrentApp_activeDialogSearchM1Produit()
        set_Current_startTextSearchM1Produit("")
        clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID()
    }

    private fun setIn_CurrentApp_activeDialogSearchM1Produit(value: Boolean) {
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

    fun clear_CurrentApp_activeDialogSearchM1Produit() {
        repo9AppCompt.currentAppCompt.let {
            if (it != null) {
                repo9AppCompt.upsert(
                    it.copy(
                        activeDialogSearchM1Produit = false
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

    fun setIN_M9CurrentApp_onVentM8BonVentKey(m8BonVent: M8BonVent) {
        repo9AppCompt.currentAppCompt.let {
            if (it != null) {
                repo9AppCompt.upsert(
                    it.copy(
                        onVentM8BonVentKey = m8BonVent.keyID,
                        onVentM8BonVentDebugInfos = m8BonVent.get_DebugInfos()
                    )
                )
            }
        }
    }

    fun setIN_CurrentApp_current_OnVent_M14VentPeriode_KeyID(m14VentPeriode: M14VentPeriode) {
        setIN_CurrentApp_M9_ActiveKeyId(current_OnVent_M14VentPeriode_KeyID = m14VentPeriode.keyID)
    }


    private fun setIN_CurrentApp_M9_ActiveKeyId(
        current_OnVent_M14VentPeriode_KeyID: String?
    ) {
        repo9AppCompt.currentAppCompt.let {
            if (it != null) {
                current_OnVent_M14VentPeriode_KeyID?.let { current_OnVent_M14VentPeriode_KeyID ->
                    it.copy(
                        current_OnVent_M14VentPeriode_KeyID = current_OnVent_M14VentPeriode_KeyID,
                    )
                }?.let { it2 ->
                    repo9AppCompt.upsert(
                        it2
                    )
                }
            }
        }
    }


}
