package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Utilisateur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import java.io.File

data class ActiveCentralValues(
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

    //-----------------Facade Boutique -------------------------------------------------------------------------------------------------------------------------
    var expanded_M3CouleurProduitInfos: M3CouleurProduitInfos? = null,
    var expanded_M1Produit: ArticlesBasesStatsTable? = null,

    var hide_prix_lence_vent_buttons: Boolean = false,
    //-----------------Fragmet.Gps Clients-------------------------------------------------------------------------------------------------------------------------
    var active_drop_down_filter_client: String = "Last Trx == Command Confirme ",

    //-----------------Fragmet.Paye-------------------------------------------------------------------------------------------------------------------------
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
    var affiche_dialoge_add_temp_travaille: Boolean = false,
    var jour_traville_ouvert_pour_add: Boolean = false,

    //-----------------Fragement.Achats.-------------------------------------------------------------------------------------------------------------------------
    val active_ModeEditesProduit: ModeEditesProduit? = ModeEditesProduit.PrixHanled,
    //-----------------FastSearcher-------------------------------------------------------------------------------------------------------------------------
    var le_pourvoire_clike_checked_est_active: Boolean = false,

    val fastSearchProduitPourVent: String = "",
    val affiche_Dialog_Fast_Affiche_Panie: Boolean = if (M18CentralParametresOfAllApps().au_Lence_Set_Compt_Ac_KeyId
        == M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId)
        false else
        false,

    val startIntOffset_PresistantFABs: IntOffset =  IntOffset(650,-500),
    var affiche_Produit_OnGrid: Boolean = true,

    //-----------------Etudiont -------------------------------------------------------------------------------------------------------------------------
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
        data class SearchProduit(val produit: ArticlesBasesStatsTable) :
            RoleDefinieParSourceACetteFragment()
    }

    // Nouvelle fonction helper pour vérifier si un filtre est actif
    fun hasActiveFilter(filter: ActiveFilter): Boolean {
        return activeFilters.contains(filter)
    }

    // Nouvelle fonction pour ajouter/retirer un filtre
    fun toggleFilter(filter: ActiveFilter): ActiveCentralValues {
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

// NEW: Sort mode enum for enhanced sorting capabilities
enum class SortVentMode {
    PAR_Creation_Vent,      // Sort by position_store_3jamale (warehouse position)
    PAR_ENTREE,          // Sort alphabetically by product name
    PAR_DERNIERE_UPDATE_LENCE  // Sort by last_update_premier_Check_Donne_TimeTamps (most recent verification)
}
