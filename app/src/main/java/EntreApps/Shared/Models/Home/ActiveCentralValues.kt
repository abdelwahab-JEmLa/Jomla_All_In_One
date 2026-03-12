package EntreApps.Shared.Models.Home

import Application4.App.Fragment.ID1.Fragment.Filter.FilterState_Facad_Boutique
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import EntreApps.Shared.Models.Components.Utilisateur
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Z_AppCompt
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.FilterState_Facad_Boutique_FragId5
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import java.io.File
import java.util.Calendar

data class ActiveCentralValues(
    val roleDefinieParSourceACetteFragment: RoleDefinieParSourceACetteFragment? = null,
    val active_OpnerDialog_M17MessageVocale: M17MessageVocale? = null,

    val handled_M10OperationVent_Pour_Link: M10OperationVentCouleur? = null,
    val affiche_Panier_au_Search_Dialog: Boolean = false,

    //-----------------Delete.Safe-------------------------------------------------------------------------------------------------------------
    val affiche_DeleteButtons: Boolean = false,
    val list_clients_por_suprime: List<M2Client> = emptyList(),


    //-----------------Produit-------------------------------------------------------------------------------------------------------------
    val active_Catalogue_Pour_NewAddedProduit: M21CataloguesCategorie? = M21CataloguesCategorie(
        keyID = "t1",
        id = 1,
        nom = "Confiserie",
        premierCategorieId = 1755942577975,
        position = 2,
        couleur = Color(0xFFFF9800)
    ),
    val active_EtateDispoNonDifinieAuAddNew: Boolean = false,

    //-----------------Peride-------------------------------------------------------------------------------------------------------------------------
    val held_Period_Pour_copie_Leur_Vents: M14VentPeriode? = null,

    //-----------------M2-----------------------------------------------------------------------------------------------------------------------------------------
    val activeOnVent_M2Client: M2Client? = null,

    //-----------------M3-----------------------------------------------------------------------------------------------------------------------------------------
    val onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent: List<M10OperationVentCouleur>? = null,

    //-----------------Bon8-----------------------------------------------------------------------------------------------------------------------------------------
    val activeOnVent_M8BonVent: M8BonVent? = null,
    val filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod: List<M8BonVent>? = null,

    //----------------------------------------------------------------------------------------------------------------------------------------------------------

    val click_On_Marque: Click_On_Marque = Click_On_Marque.Standart,
    val actuelle_Ciblage_MaxPosition: Int = 1,
    val gps_follow_mode_active: Boolean? = false,

    val visibleClientsNow: MapClientsViewModel.VisibleClientsNow? = null,

    val isInTemporaryShowAllMode: Boolean = false,

    //-----------------M9-----------------------------------------------------------------------------------------------------------------------------------------
    val activeCompt_KeyID: String? = M18CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId,
    val activeCompt: Z_AppCompt? = null,

    // Derived from activeCompt.its_Admin — set by FocusedValues_FluidApp flow, never set manually
    val currentApp_Est_Admin: Boolean = false,
    val currentApp_ItsWorkChezGrossisst: Boolean = false,

    //-----------------Repo11AchatOperation-------------------------------------------------------------------------------------------------------------------------
    val active_M14VentPeriode_AuFilterAchats: M14VentPeriode? = null,
    val active_M15Grossist_AuFilterAchats: M15Grossist? = null,
    val active_M2Client_AuFilterAchats: M2Client? = null,
    val active_M1Produit_AuFilterAchats: M01Produit? = null,

    val show_Dialog_filter_AChats_Par_Client_Acheteur: Boolean? = false,
    val vent_Au_Dialog_filter_AChats_Par_Client_Acheteur: M14VentPeriode? = null,

    val dialog_achats_ventPeriod: M14VentPeriode? = null,
    //-----------------m14-------------------------------------------------------------------------------------------------------------------------
    val active_M14VentPeriode: M14VentPeriode? = null,

    //-----------------Grossist-------------------------------------------------------------------------------------------------------------------------
    val image_Flotant: File? = null,

//-----------------By.Fragments-------------------------------------------------------------------------------------------------------------------------

    //-----------------Starte Classe -------------------------------------------------------------------------------------------------------------
    var mainInitDataBaseProgressEtate: Float = 0f,

//-----------------Floating Abouve AL-------------------------------------------------------------------------------------------------------------------------
    var afficheFloatingOutlinedSearcher_of_Achat: Boolean = false,
    val outlined_filter_searcher_floating_abouve_all: String = "",

    //-----------------FacadeBoutiqueElectro -------------------------------------------------------------------------------------------------------------------------
    var expanded_M3CouleurProduitInfos: M3CouleurProduitInfos? = null,
    var expanded_M1Produit: M01Produit? = null,

    var hide_prix_lence_vent_buttons: Boolean = false,

    var filterState_Facad_Boutique: FilterState_Facad_Boutique? = null,
    var filterState_Facad_Boutique_FragId5: FilterState_Facad_Boutique_FragId5? = null,

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

    val startIntOffset_PresistantFABs: IntOffset = IntOffset(650, -500),
    var affiche_Produit_OnGrid: Boolean = true,

    //-----------------Tahfide_quran -------------------------------------------------------------------------------------------------------------------------
    val displaye_dialog_mois_moinAcPlus_6_du_current: Boolean = false,
    val displaye_sections_education_du_mois: Calendar? = null,

    val filter_les_absents: Boolean = false,

    //-----------------Fast.PAnie-------------------------------------------------------------------------------------------------------------------------
    val markerStatusDialogActiveM2Client: M2Client? = null,

    val activeFilters: Set<ActiveFilter> = emptySet(),

    val held_Produit_Pour_Move_Au_Position_Store: M01Produit? = null,
    val affiche_CheckList_ChoisiseurActiveFilter: Boolean = false,

    val sortVentsParClassment: Boolean = false,
    val sortVentMode: SortVentMode? = null,

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
    //-----------------mutableStateOf lightweight vars -------------------------------------------------------------------------------------------------------------------------
    var isControleFabVisible: Boolean by mutableStateOf(false)

    //-------------------------------------------------------------------------------------------------------------------------------

    companion object {
        fun get_Default(): ActiveCentralValues {
            return ActiveCentralValues()
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
        data class SearchProduit(val produit: M01Produit) :
            RoleDefinieParSourceACetteFragment()
    }

    fun hasActiveFilter(filter: ActiveFilter): Boolean {
        return activeFilters.contains(filter)
    }

    fun toggleFilter(filter: ActiveFilter): ActiveCentralValues {
        return if (activeFilters.contains(filter)) {
            this.copy(activeFilters = activeFilters - filter)
        } else {
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
    PAR_Creation_Vent,
    PAR_ENTREE,
    PAR_DERNIERE_UPDATE_LENCE
}
