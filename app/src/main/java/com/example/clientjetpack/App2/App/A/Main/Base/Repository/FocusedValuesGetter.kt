// FocusedValuesGetter_app2.kt - FIXED VERSION
package com.example.clientjetpack.App2.App.A.Main.Base.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Ousstad_Tahfid
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
import V.DiviseParSections.App.Shared.Repository.Repo21.Repository.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import com.example.clientjetpack.App2.App.B.Fragment.Filter.FilterState_Facad_Boutique_app2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

data class ActiveCentralValues_app2(
    val roleDefinieParSourceACetteFragment: RoleDefinieParSourceACetteFragment? = null,
    val active_OpnerDialog_M17MessageVocale: M17MessageVocale? = null,

    val handled_M10OperationVent_Pour_Link: M10OperationVentCouleur? = null,
    val affiche_Panier_au_Search_Dialog: Boolean = false,

    //-----------------Delete.Safe-------------------------------------------------------------------------------------------------------------
    val affiche_DeleteButtons: Boolean = false,
    val list_clients_por_suprime: List<M2Client> = emptyList(),

    //-----------------Produit-------------------------------------------------------------------------------------------------------------
    val active_Catalogue_Pour_NewAddedProduit: CataloguesCaegorie? = CataloguesCaegorie(
        keyID = "t1",
        id = 1,
        nom = "Confiserie",
        premierCategorieId = 1755942577975,
        position = 2,
        couleur = Color(0xFFFF9800) // Orange for confectionery
    ),
    val active_EtateDispoNonDifinieAuAddNew: Boolean = false,

    //-----------------Peride-------------------------------------------------------------------------------------------------------------------------
    val held_Period_Pour_copie_Leur_Vents: M14VentPeriode? = null,

    //-----------------Bon8-----------------------------------------------------------------------------------------------------------------------------------------
    val click_On_Marque: Click_On_Marque = Click_On_Marque.Standart,
    val actuelle_Ciblage_MaxPosition: Int = 1,
    val gps_follow_mode_active: Boolean? = false,

    val visibleClientsNow: MapClientsViewModel.VisibleClientsNow? = null,

    // FIXED: Add flag to track if we're in temporary mode
    val isInTemporaryShowAllMode: Boolean = false,

    //-----------------Repo11AchatOperation-------------------------------------------------------------------------------------------------------------------------
    val active_M14VentPeriode_AuFilterAchats: M14VentPeriode? = null,
    val active_M15Grossist_AuFilterAchats: M15Grossist? = null,
    val active_M2Client_AuFilterAchats: M2Client? = null,
    val active_M1Produit_AuFilterAchats: ArticlesBasesStatsTable? = null,

    val show_Dialog_filter_AChats_Par_Client_Acheteur: Boolean? = false,
    val vent_Au_Dialog_filter_AChats_Par_Client_Acheteur: M14VentPeriode? = null,

    val dialog_achats_ventPeriod: M14VentPeriode? = null,
    //-----------------Grossist-------------------------------------------------------------------------------------------------------------------------
    val image_Flotant: File? = null,

//-----------------By.Fragments-------------------------------------------------------------------------------------------------------------------------
    //-----------------Floating Abouve AL-------------------------------------------------------------------------------------------------------------------------
    var afficheFloatingOutlinedSearcher_of_Achat: Boolean = false,
    val outlined_filter_searcher_floating_abouve_all: String = "",

    //-----------------FacadeBoutiqueElectro -------------------------------------------------------------------------------------------------------------------------
    var expanded_M3CouleurProduitInfos: M3CouleurProduitInfos? = null,
    var expanded_M1Produit: ArticlesBasesStatsTable? = null,

    var hide_prix_lence_vent_buttons: Boolean = false,

    var FilterState_Facad_Boutique_app2: FilterState_Facad_Boutique_app2? = null,


    //-----------------Fragmet.Gps Clients-------------------------------------------------------------------------------------------------------------------------
    var active_drop_down_filter_client: String = "Last Trx == Command Confirme ",

    //-----------------Fragmet.Paye-------------------------------------------------------------------------------------------------------------------------
    var active_Ousstad_Tahfid: Ousstad_Tahfid? = run {
        val params = M18CentralParametresOfAllApps()
        val utilisateur = when (params.au_Lence_Set_Compt_Ac_KeyId) {
            params.abdelmomen_Compt_KeyId -> Utilisateur.Abdelmoumen
            params.walid_Compt_KeyId -> Utilisateur.Walid
            params.abdelwahabTravailleChezGros_KeyId -> Utilisateur.Abdelwahab_Osstad
            params.amine_madrasa_Compt_KeyId -> Utilisateur.Amine_Madrassa
            else -> Utilisateur.Admin
        }

        // FIXED: Map Utilisateur to Ousstad_Tahfid
        when (utilisateur) {
            Utilisateur.Abdelwahab_Osstad -> Ousstad_Tahfid.Abdelwahab_Osstad
            Utilisateur.Amine_Madrassa -> Ousstad_Tahfid.Amine_Madrassa
            Utilisateur.Admin -> null
            else -> null
        }
    },

    var active_filter_du_utilisateur: Utilisateur? = run {
        val params = M18CentralParametresOfAllApps()
        when (params.au_Lence_Set_Compt_Ac_KeyId) {
            params.abdelmomen_Compt_KeyId -> Utilisateur.Abdelmoumen
            params.walid_Compt_KeyId -> Utilisateur.Walid
            params.abdelwahabTravailleChezGros_KeyId -> Utilisateur.Abdelwahab_Osstad
            params.amine_madrasa_Compt_KeyId -> Utilisateur.Amine_Madrassa
            else -> Utilisateur.Admin
        }
    },

    //-----------------Tahfide_quran -------------------------------------------------------------------------------------------------------------------------

    var affiche_dialoge_add_temp_travaille: Boolean = false,
    var jour_traville_ouvert_pour_add: Boolean = false,

    //-----------------Fragement.Achats.-------------------------------------------------------------------------------------------------------------------------
    val active_ModeEditesProduit: ModeEditesProduit? = ModeEditesProduit.PrixHanled,
    //-----------------FastSearcher-------------------------------------------------------------------------------------------------------------------------
    val bons_a_imprime_avec_image_produit: List<M8BonVent> = emptyList(),

    var le_pourvoire_clike_checked_est_active: Boolean = false,

    val fastSearchProduitPourVent: String = "",
    val affiche_Dialog_Fast_Affiche_Panie: Boolean = if (M18CentralParametresOfAllApps().au_Lence_Set_Compt_Ac_KeyId
        == M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId)
        false else
        false,

    val startIntOffset_PresistantFABs: IntOffset =  IntOffset(650,-500),
    var affiche_Produit_OnGrid: Boolean = true,

    //-----------------Tahfide_quran -------------------------------------------------------------------------------------------------------------------------
    val displaye_dialog_mois_moinAcPlus_6_du_current: Boolean = false,
    val displaye_sections_education_du_mois: Calendar? = null,

    val filter_les_absents: Boolean = false,

    //-----------------Fast.PAnie-------------------------------------------------------------------------------------------------------------------------
    val markerStatusDialogActiveM2Client: M2Client? = null,

    val activeFilters: Set<ActiveFilter> = emptySet(),

    val held_Produit_Pour_Move_Au_Position_Store: ArticlesBasesStatsTable? =null,
    val affiche_CheckList_ChoisiseurActiveFilter: Boolean = false,

    // UPDATED: Keep backward compatibility while adding new sort mode
    val sortVentsParClassment: Boolean = false,
    val sortVentMode: SortVentMode? = null,  // New field for enhanced sorting

    //-----------------Fabs.Affichage-------------------------------------------------------------------------------------------------------------------------
    val affiche_Floating_Button_TogleFilterMarquers: Boolean = true,
    val affiche_Floating_Button_Cible_Client: Boolean = true,

    val affiche_Floating_Button_gps_follow_mode_active: Boolean = true,
    val affiche_Floating_Button_AddCLient: Boolean = false,

    val affiche_Floating_Button_SelecteCategorieEtAddNewProduit: Boolean = false,
    val affiche_Floating_Button_FABsModeEditesProduit: Boolean = false,

    //-----------------Fabs.Affichage-------------------------------------------------------------------------------------------------------------------------
    var pourcentage_AffichageDuCatalogue_Conficerie: Double = 0.0,
    var pourcentage_AffichageDuCatalogue_Cosmitiques: Double = 0.0,
    var pourcentage_AffichageDuCatalogue_tebnage: Double = 0.0,

    ) {
    companion object {
        fun get_Default(): ActiveCentralValues_app2 {
            return ActiveCentralValues_app2()
        }
    }

    enum class ModeEditesProduit(val couleur: Color = Color(0xFF000000)) {
        Standart,
        PrixHanled(Color(0xFFFF5722)),
    }

    enum class Click_On_Marque(val couleur: Color = Color(0xFF000000)) {
        Standart,
        Call(Color(0xFF3F51B5)),
        Navigate(Color(0xFFFFEB3B)),
        Marck_Ferme(Color(0xFF5C5C51)),
        Marck_Command_Livret(Color(0xFF2196F3)),
        ADD_Au_Ciblage_Clients(Color(0xFFFF5722)),
        Affiche_OnCommand_VentPeriod_Transaction(Color(0xFF9C27B0)),
    }

    sealed class RoleDefinieParSourceACetteFragment {
        data object AfficheSearchAllProduits : RoleDefinieParSourceACetteFragment()
        data class SearchProduit(val produit: ArticlesBasesStatsTable) :
            RoleDefinieParSourceACetteFragment()
    }

    // Nouvelle fonction helper pour vérifier si un filtre est actif
    fun hasActiveFilter(filter: ActiveFilter): Boolean {
        return activeFilters.contains(filter)
    }

    // Nouvelle fonction pour ajouter/retirer un filtre
    fun toggleFilter(filter: ActiveFilter): ActiveCentralValues_app2 {
        return if (activeFilters.contains(filter)) {
            // Retirer le filtre
            this.copy(activeFilters = activeFilters - filter)
        } else {
            // Ajouter le filtre
            this.copy(activeFilters = activeFilters + filter)
        }
    }

    sealed class ActiveFilter {
        data object NonTrouve : ActiveFilter()
        data object PrixAuGerant : ActiveFilter()
        data object premier_Check_Donne : ActiveFilter()
        data object non_premier_Check_Donne : ActiveFilter()
    }


}

enum class SortVentMode {
    PAR_Creation_Vent,      // Sort by position_store_3jamale (warehouse position)
    PAR_ENTREE,          // Sort alphabetically by product name
    PAR_DERNIERE_UPDATE_LENCE  // Sort by last_update_premier_Check_Donne_TimeTamps (most recent verification)
}

@Stable
class FocusedValuesGetter_app2(
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

    private val _ActiveCentralValues_app2 = mutableStateOf(
        ActiveCentralValues_app2(
            active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode()
        )
    )
    val active_Central_Values by derivedStateOf { _ActiveCentralValues_app2.value }

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

    // FIXED: Properly implemented computedVisibleClientsMode with timing control
    val computedVisibleClientsMode by derivedStateOf {
        val activeClientInfos = activeOnVentM2ClientInfos
        val currentValues = active_Central_Values

        // If there's an explicit setting and we're not in temporary mode, use it
        if (currentValues.visibleClientsNow != null && !currentValues.isInTemporaryShowAllMode) {
            return@derivedStateOf currentValues.visibleClientsNow!!
        }

        // If we're in temporary mode, always show all
        if (currentValues.isInTemporaryShowAllMode) {
            return@derivedStateOf MapClientsViewModel.VisibleClientsNow.showAll
        }

        // Default logic based on admin status and special account handling
        val params = M18CentralParametresOfAllApps()
        when {
            // Special handling for abdelmomen account - always show command/delivery filter
            params.au_Lence_Set_Compt_Ac_KeyId == params.abdelmomen_Compt_KeyId -> {
                // Update label if not already set correctly
                if (!currentValues.active_drop_down_filter_client.startsWith("COMMANDE_LIVRAI")) {
                    update_ActiveCentralValues_app2(
                        currentValues.copy(
                            active_drop_down_filter_client = "COMMANDE_LIVRAI Filter"
                        )
                    )
                }
                MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter
            }
            // For admin users, show all
            currentApp_Est_Admin -> {
                // Update label if not already set correctly
                if (currentValues.active_drop_down_filter_client != "Show All Clients") {
                    update_ActiveCentralValues_app2(
                        currentValues.copy(
                            active_drop_down_filter_client = "Show All Clients"
                        )
                    )
                }
                MapClientsViewModel.VisibleClientsNow.showAll
            }
            // For non-admin users, show targeted clients
            else -> {
                // Update label if not already set correctly
                if (currentValues.active_drop_down_filter_client != "Targeted Clients") {
                    update_ActiveCentralValues_app2(
                        currentValues.copy(
                            active_drop_down_filter_client = "Targeted Clients"
                        )
                    )
                }
                MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
            }
        }
    }

    // Method to get the current visible clients mode
    fun getCurrentVisibleClientsMode(): MapClientsViewModel.VisibleClientsNow {
        return computedVisibleClientsMode
    }

    // FIXED: Enhanced temporary mode handling with proper timing control (2 seconds)
    fun handleTemporaryShowAllMode() {
        // Cancel any existing timer
        temporaryModeJob?.cancel()

        val currentValues = active_Central_Values

        // Set to temporary show all mode
        update_ActiveCentralValues_app2(
            currentValues.copy(
                visibleClientsNow = MapClientsViewModel.VisibleClientsNow.showAll,
                isInTemporaryShowAllMode = true
            )
        )

        // FIXED: Start new timer for exactly 2 seconds (was 7 seconds before)
        temporaryModeJob = coroutineScope.launch {
            delay(2000) // 2 seconds instead of 7

            // Check if we're still in temporary mode (user might have changed it manually)
            if (_ActiveCentralValues_app2.value.isInTemporaryShowAllMode) {
                revertToStandardMode()
            }
        }
    }

    fun revertToStandardMode() {
        // Cancel any running timer
        temporaryModeJob?.cancel()

        val currentValues = active_Central_Values
        val params = M18CentralParametresOfAllApps()

        val standardMode = when {
            // Special handling for abdelmomen account
            params.au_Lence_Set_Compt_Ac_KeyId == params.abdelmomen_Compt_KeyId ->
                MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter
            // For admin users, show all
            currentApp_Est_Admin ->
                MapClientsViewModel.VisibleClientsNow.showAll
            // For non-admin users, show targeted clients
            else ->
                MapClientsViewModel.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR
        }

        update_ActiveCentralValues_app2(
            currentValues.copy(
                visibleClientsNow = standardMode,
                isInTemporaryShowAllMode = false
            )
        )
    }

    // FIXED: Add method to handle activeOnVentM2ClientInfos changes automatically
    fun handleActiveClientChange(newActiveClient: M2Client?) {
        if (newActiveClient != null) {
            // Start temporary show all mode when client becomes active
            handleTemporaryShowAllMode()
        } else {
            // Immediately revert when no active client
            revertToStandardMode()
        }
    }

    fun update_ActiveCentralValues_app2(new: ActiveCentralValues_app2) {
        _ActiveCentralValues_app2.value = new
    }

    fun removePeriodFilter() {
        val currentValues = active_Central_Values
        val updatedValues =
            currentValues.copy(active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode())
        update_ActiveCentralValues_app2(updatedValues)
    }

    fun addGrossistFilter(grossist: M15Grossist) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M15Grossist_AuFilterAchats = grossist)
        update_ActiveCentralValues_app2(updatedValues)
    }

    fun removeGrossistFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M15Grossist_AuFilterAchats = null)
        update_ActiveCentralValues_app2(updatedValues)
    }

    fun addClientFilter(client: M2Client) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M2Client_AuFilterAchats = client)
        update_ActiveCentralValues_app2(updatedValues)
    }

    fun removeClientFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M2Client_AuFilterAchats = null)
        update_ActiveCentralValues_app2(updatedValues)
    }

    fun addProductFilter(product: ArticlesBasesStatsTable) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M1Produit_AuFilterAchats = product)
        update_ActiveCentralValues_app2(updatedValues)
    }

    fun removeProductFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M1Produit_AuFilterAchats = null)
        update_ActiveCentralValues_app2(updatedValues)
    }

    fun clearAllFilters() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(
            active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode(),
            active_M15Grossist_AuFilterAchats = null,
            active_M2Client_AuFilterAchats = null,
            active_M1Produit_AuFilterAchats = null
        )
        update_ActiveCentralValues_app2(updatedValues)
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
        M18CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId == M18CentralParametresOfAllApps.get_Default().younes_Compt_KeyId
                || M18CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId == M18CentralParametresOfAllApps.get_Default().abdelmomen_Compt_KeyId
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

}
