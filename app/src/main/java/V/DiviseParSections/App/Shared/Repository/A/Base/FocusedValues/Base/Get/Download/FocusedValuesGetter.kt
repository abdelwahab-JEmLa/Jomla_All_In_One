// FocusedValuesGetter.kt - FIXED VERSION
package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download

import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos.TypeChoisi
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import Z_CodePartageEntreApps.DataBase.Repo18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import EntreApps.Shared.Models.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import EntreApps.Shared.Models.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@Stable
class FocusedValuesGetter(
    repo2Client: Repo2Client,
    repoM1ProduitInfos: RepoM1Produit,
    val repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
    repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo13TarificationInfos: Repo13TarificationInfos,
     val repo14VentPeriode: Repo14VentPeriode,
    private val repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps,
) {
    // FIXED: Add coroutine scope and job for timing control
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var temporaryModeJob: Job? = null

    val currentActiveFocuced_M14VentPeriode by derivedStateOf {
        val periods = repo14VentPeriode.datasValue
        if (periods.isEmpty()) return@derivedStateOf null
        periods.find { it.keyID == currentActive_M9AppCompt?.current_OnVent_M14VentPeriode_KeyID }
            ?: periods.lastOrNull()
    }

    val filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod by derivedStateOf {
        val currentPeriod =
            currentActiveFocuced_M14VentPeriode ?: return@derivedStateOf emptyList<M8BonVent>()
        repo8BonVent.datasValue.filter { it.parent_M14VentPeriod_KeyId == currentPeriod.keyID }
    }
    /**
     * Adds a new M10OperationVentCouleur to the repository
     * @param operation The operation to add
     */
    fun ajoute_New_M10OperationVentCouleur(operation: M10OperationVentCouleur) {
        repo10OperationVentCouleur.add_New(operation)
    }
    fun find_M3CouleurInfos_By_KeyID(keyId: String): M3CouleurProduitInfos? =
        repo3CouleurProduitInfos.datasValue.find { it.keyID == keyId }


    /**
     * Updates an existing M10OperationVentCouleur
     * @param operation The operation to update
     */
    fun update_M10OperationVentCouleur(operation: M10OperationVentCouleur) {
        repo10OperationVentCouleur.update_If_Exist(operation)
    }

    /**
     * Opens the dialog for choosing quantity for a specific color/operation
     * @param operation The M10OperationVentCouleur to open dialog for
     */
    fun active_M3Couleur_pour_ouvrire_son_Dialog_choixQuantity(operation: M10OperationVentCouleur) {
        val currentAppCompt = repo9AppCompt.currentAppCompt ?: return

        // Update the app compte with the operation's keyID to trigger dialog
        val updatedAppCompt = currentAppCompt.copy(
            onVentM3CouleurProduitInfosKeyID = operation.keyID,
            dialogChoisireQuantityM1ProduitInfosKeyID = operation.parent_M1Produit_KeyId
        )

        repo9AppCompt.upsert(updatedAppCompt)
    }
    val filtered_ListM10Vent_BY_Curr_M14VentPeriod by derivedStateOf {
        val currentPeriod = currentActiveFocuced_M14VentPeriode
            ?: return@derivedStateOf emptyList<M10OperationVentCouleur>()
        repo10OperationVentCouleur.datasValue.filter { it.parent_M14VentPeriod_KeyId == currentPeriod.keyID }
    }

    fun getDefaultM8BonVent() = M8BonVent(
        keyID = M8BonVent.generePushKey(),
        parent_M9AppCompt_KeyID = currentActive_M9AppCompt?.keyID ?: "",
        parent_M14VentPeriod_KeyId = currentActiveFocuced_M14VentPeriode?.keyID ?: "null",
    )

    private val _activeCentralValues = mutableStateOf(
        ActiveCentralValues(
            active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode()
        )
    )
    val active_Central_Values by derivedStateOf { _activeCentralValues.value }

    private fun getCurrentActiveVentPeriode(): M14VentPeriode? {
        return try {
            val currentAppCompt = repo9AppCompt.datasValue.firstOrNull {
                it.keyID == repo18CentralParametresOfAllApps.dataValue?.au_Lence_Set_Compt_Ac_KeyId
            }
            val periods = repo14VentPeriode.datasValue
            if (periods.isEmpty()) return null
            periods.find { it.keyID == currentAppCompt?.current_OnVent_M14VentPeriode_KeyID }
                ?: periods.lastOrNull()
        } catch (e: Exception) {
            null
        }
    }


    fun update_activeCentralValues(new: ActiveCentralValues) {
        _activeCentralValues.value = new
    }
    fun update_oneMutableStateLesseRessources(isControleFabVisible: Boolean) {
        _activeCentralValues.value.isControleFabVisible = isControleFabVisible
    }

    fun removePeriodFilter() {
        val currentValues = active_Central_Values
        val updatedValues =
            currentValues.copy(active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode())
        update_activeCentralValues(updatedValues)
    }

    fun addGrossistFilter(grossist: M15Grossist) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M15Grossist_AuFilterAchats = grossist)
        update_activeCentralValues(updatedValues)
    }

    fun removeGrossistFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M15Grossist_AuFilterAchats = null)
        update_activeCentralValues(updatedValues)
    }

    fun addClientFilter(client: M2Client) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M2Client_AuFilterAchats = client)
        update_activeCentralValues(updatedValues)
    }

    fun removeClientFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M2Client_AuFilterAchats = null)
        update_activeCentralValues(updatedValues)
    }

    fun addProductFilter(product: M01Produit) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M1Produit_AuFilterAchats = product)
        update_activeCentralValues(updatedValues)
    }

    fun removeProductFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M1Produit_AuFilterAchats = null)
        update_activeCentralValues(updatedValues)
    }

    fun clearAllFilters() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(
            active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode(),
            active_M15Grossist_AuFilterAchats = null,
            active_M2Client_AuFilterAchats = null,
            active_M1Produit_AuFilterAchats = null
        )
        update_activeCentralValues(updatedValues)
    }
    //-----------------------------M9AppCompt-----------------------------------------------------------------------------------------------------------------------------------------------
    val currentActive_M9AppCompt by derivedStateOf {
        repo9AppCompt.datasValue.firstOrNull {
            it.keyID == repo18CentralParametresOfAllApps.dataValue?.au_Lence_Set_Compt_Ac_KeyId
        }
    }

    val currentApp_Est_Admin by derivedStateOf {
        currentActive_M9AppCompt?.its_Admin == true
    }

    val currentApp_ItsWorkChezGrossisst by derivedStateOf {
        currentActive_M9AppCompt?.travailleChezGrossisst3Ali == true
    }


    val currentApp_Its_Vendeur by derivedStateOf {
        M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId == M00CentralParametresOfAllApps.get_Default().younes_Compt_KeyId
                || M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId == M00CentralParametresOfAllApps.get_Default().abdelmomen_Compt_KeyId
    }


    //-----------------------------M8BonVent-----------------------------------------------------------------------------------------------------------------------------------------------
    val activeOnVent_M8BonVent by derivedStateOf {
        repo8BonVent.datasValue.find { it.keyID == currentActive_M9AppCompt?.onVentM8BonVentKey }
    }

    val filteredList_M2Client_Ou_Leur_Last_M8BonVent_Etate_IS_Cible by derivedStateOf {
        repo2Client.datasValue.filter { client ->
            filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
                .sortedBy { it.creationTimestamps }
                .lastOrNull { it.parent_M2Client_KeyID == client.keyID }
                ?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible
        }
    }

    val filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT by derivedStateOf {
        repo2Client.datasValue.filter { client ->
            val lastBonVent = filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
                .filter {
                    (it.parent_M2Client_KeyID == client.keyID && it.parent_M9AppCompt_KeyID == (currentActive_M9AppCompt?.keyID
                        ?: ""))
                }
                .maxByOrNull { it.creationTimestamps }

            lastBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT ||
                    lastBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.Rapport_Entre_On_Etate_De_Bloquage
        }
    }

    val activeOnVentM2ClientInfos by derivedStateOf {
        repo2Client.datasValue.find {
            it.keyID == (activeOnVent_M8BonVent?.parent_M2Client_KeyID ?: "")
        }
    }

    val activeOnVent_M2Client by derivedStateOf {
        val targetKey = activeOnVent_M8BonVent?.parent_M2Client_KeyID
        repo2Client.datasValue.find { it.keyID == targetKey }
    }

    fun getDefaultM10VentOperation(): M10OperationVentCouleur? {
        return activeOnVent_M8BonVent?.let {
            with(it) {
                M10OperationVentCouleur(
                    parent_M14VentPeriod_KeyId = parent_M14VentPeriod_KeyId,
                    parent_M8BonVent_KeyId = keyID,
                    parent_M8BonVent_DebugInfos = get_DebugInfos(),
                )
            }
        }
    }

    val onVentM10VentOperation by derivedStateOf {
        repo10OperationVentCouleur.datasValue.find { it.keyID == currentActive_M9AppCompt?.onVentM3CouleurProduitInfosKeyID }
    }

    //-----------------------------M10OperationVentCouleur-----------------------------------------------------------------------------------------------------------------------------------------------
    val onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent by derivedStateOf {
        repo10OperationVentCouleur.datasValue.filter {
            it.parent_M8BonVent_KeyId == (currentActive_M9AppCompt?.onVentM8BonVentKey ?: "")
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    val focused_M1ProduitInfos_Pour_PrixDifineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find { it.keyID == currentActive_M9AppCompt?.activeFocuce_TariffPrixDifineur_M1ProduitKeyID }
    }

    fun get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(produit: M01Produit) =
        onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.filter { ventOperation ->
            ventOperation.parent_M1Produit_KeyId == produit.keyID
        }

    val focused_ListM10OpeVentCouleur_Par_PD_M1Produit by derivedStateOf {
        onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.filter {
            it.parent_M1Produit_KeyId == (focused_M1ProduitInfos_Pour_PrixDifineur?.keyID ?: "")
        }
    }

    val active_M1ProduitInfos_In_CurCompt_DialogQantity_Defineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find {
            it.keyID == (currentActive_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID ?: "")
        }
    }

    val focused_M13TarificationInfos_Pour_Produit by derivedStateOf {
        repo13TarificationInfos.datasValue.lastOrNull { tariff ->
            tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                    tariff.parent_M1Produit_KeyId == (focused_M1ProduitInfos_Pour_PrixDifineur?.keyID
                ?: "")
        }
    }

    val activeDialogSearchM1Produit by derivedStateOf {
        currentActive_M9AppCompt?.activeDialogSearchM1Produit ?: false
    }

    val its_Developing_Mode = M00CentralParametresOfAllApps.get_Default().itsDevMode

    val m00CentralParametresOfAllApps by derivedStateOf { M00CentralParametresOfAllApps() }

    companion object {
        @SuppressLint("ModifierFactoryUnreferencedReceiver")
        fun Modifier.getSemanticsTagFocucedVars(getter: FocusedValuesGetter): Modifier {
            val map = buildMap {
                with(getter) {
                    put("onVentM2ClientInfos", activeOnVent_M2Client?.let { it.nom } ?: "null")
                    put(
                        "onVentM8BonVent",
                        activeOnVent_M8BonVent?.let { "${it.parent_M2Client_DebugInfos}/${it.etateActuellementEst}" }
                            ?: "null")
                    put(
                        "focused_M1ProduitInfos_Pour_PrixDifineur",
                        focused_M1ProduitInfos_Pour_PrixDifineur?.let { it.nom + it.keyID }
                            ?: "null")
                    put(
                        "onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent",
                        onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.map { "${it.parent_M1Produit_DebugInfos} / ${it.parent_M1Produit_KeyId}" })
                    put(
                        "focused_ListM10OpeVentCouleur_Par_PD_M1Produit",
                        focused_ListM10OpeVentCouleur_Par_PD_M1Produit.map { it.getDebugInfos() })
                    put(
                        "isInTemporaryMode",
                        active_Central_Values.isInTemporaryShowAllMode.toString()
                    )
                }
            }
            return map.entries.foldIndexed(this) { index, modifier, (key, value) ->
                modifier.getSemanticsTag_By_datas_A_Affiche_Au_Nom(index + 6, key, value)
            }
        }
    }
}
