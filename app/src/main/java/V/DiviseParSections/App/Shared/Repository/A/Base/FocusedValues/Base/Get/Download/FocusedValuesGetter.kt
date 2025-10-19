// FocusedValuesGetter.kt - FIXED VERSION with requestClearAndFocusSearch
package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
class FocusedValuesGetter(
    repo2Client: Repo2Client,
    repoM1ProduitInfos: RepoM1Produit,
    repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
    repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo13TarificationInfos: Repo13TarificationInfos,
    private val repo14VentPeriode: Repo14VentPeriode,
    private val repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var temporaryModeJob: Job? = null

    // FIXED: Add references to UI controls for direct manipulation
    private var searchFocusRequester: FocusRequester? = null
    private var searchKeyboardController: SoftwareKeyboardController? = null

    // FIXED: Method to set UI controls from Composable
    fun setSearchFieldControls(
        focusRequester: FocusRequester,
        keyboardController: SoftwareKeyboardController?
    ) {
        searchFocusRequester = focusRequester
        searchKeyboardController = keyboardController
    }

    // FIXED: New method to clear and refocus search field
    fun requestClearAndFocusSearch() {
        coroutineScope.launch {
            val currentValues = active_Central_Values

            // Increment trigger to cause recomposition
            val newTrigger = currentValues.clearAndFocusTrigger + 1

            update_activeCentralValues(
                currentValues.copy(
                    clearAndFocusTrigger = newTrigger
                )
            )
        }
    }

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

    val computedVisibleClientsMode by derivedStateOf {
        val activeClientInfos = activeOnVentM2ClientInfos
        val currentValues = active_Central_Values

        if (currentValues.visibleClientsNow != null && !currentValues.isInTemporaryShowAllMode) {
            return@derivedStateOf currentValues.visibleClientsNow!!
        }

        if (currentValues.isInTemporaryShowAllMode) {
            return@derivedStateOf MapClientsViewModel.VisibleClientsNow.showAll
        }

        val params = M18CentralParametresOfAllApps()
        when {
            params.au_Lence_Set_Compt_Ac_KeyId == params.abdelmomen_Compt_KeyId ->
                MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter
            currentApp_Est_Admin ->
                MapClientsViewModel.VisibleClientsNow.showAll
            else ->
                MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
        }
    }

    fun getCurrentVisibleClientsMode(): MapClientsViewModel.VisibleClientsNow {
        return computedVisibleClientsMode
    }

    fun handleTemporaryShowAllMode() {
        temporaryModeJob?.cancel()

        val currentValues = active_Central_Values

        update_activeCentralValues(
            currentValues.copy(
                visibleClientsNow = MapClientsViewModel.VisibleClientsNow.showAll,
                isInTemporaryShowAllMode = true
            )
        )

        temporaryModeJob = coroutineScope.launch {
            delay(2000)

            if (_activeCentralValues.value.isInTemporaryShowAllMode) {
                revertToStandardMode()
            }
        }
    }

    fun revertToStandardMode() {
        temporaryModeJob?.cancel()

        val currentValues = active_Central_Values
        val params = M18CentralParametresOfAllApps()

        val standardMode = when {
            params.au_Lence_Set_Compt_Ac_KeyId == params.abdelmomen_Compt_KeyId ->
                MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter
            currentApp_Est_Admin ->
                MapClientsViewModel.VisibleClientsNow.showAll
            else ->
                MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
        }

        update_activeCentralValues(
            currentValues.copy(
                visibleClientsNow = standardMode,
                isInTemporaryShowAllMode = false
            )
        )
    }

    fun handleActiveClientChange(newActiveClient: M2Client?) {
        if (newActiveClient != null) {
            handleTemporaryShowAllMode()
        } else {
            revertToStandardMode()
        }
    }

    fun update_activeCentralValues(new: ActiveCentralValues) {
        _activeCentralValues.value = new
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

    fun addProductFilter(product: ArticlesBasesStatsTable) {
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

    val onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent by derivedStateOf {
        repo10OperationVentCouleur.datasValue.filter {
            it.parent_M8BonVent_KeyId == (currentActive_M9AppCompt?.onVentM8BonVentKey ?: "")
        }
    }

    val focused_M1ProduitInfos_Pour_PrixDifineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find { it.keyID == currentActive_M9AppCompt?.activeFocuce_TariffPrixDifineur_M1ProduitKeyID }
    }

    fun get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(produit: ArticlesBasesStatsTable) =
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

    val its_Developing_Mode = M18CentralParametresOfAllApps.get_Default().itsDevMode

    val m18CentralParametresOfAllApps by derivedStateOf { M18CentralParametresOfAllApps() }

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
                    put("computedVisibleClientsMode", computedVisibleClientsMode.toString())
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
