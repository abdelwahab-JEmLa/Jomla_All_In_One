package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
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

    //-----------------Produit-------------------------------------------------------------------------------------------------------------
    val active_Catalogue_Pour_NewAddedProduit: CataloguesCaegorie? = CataloguesCaegorie(
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

    //-----------------Bon8-----------------------------------------------------------------------------------------------------------------------------------------
    val click_On_Marque: Click_On_Marque = Click_On_Marque.Standart,
    val actuelle_Ciblage_MaxPosition: Int = 1,
    val gps_follow_mode_active: Boolean? = false,

    val visibleClientsNow: MapClientsViewModel.VisibleClientsNow? = null,
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
    //-----------------Fragmet.EditeBaseDonne.Fabs-------------------------------------------------------------------------------------------------------------------------
    val active_ModeEditesProduit: ModeEditesProduit? = ModeEditesProduit.PrixHanled,
    //-----------------Fragement.Achats.-------------------------------------------------------------------------------------------------------------------------
    var afficheFloatingOutlinedSearcher_of_Achat: Boolean = false,
    val outlined_filter_searcher_achat: String = "",
    //-----------------FastSearcher-------------------------------------------------------------------------------------------------------------------------
    val fastSearchProduitPourVent: String = "",
    // REMOVED: shouldFocusSearchTextField (replaced with clearAndFocusTrigger)
    // FIXED: New trigger-based approach instead of boolean flag
    val clearAndFocusTrigger: Long = 0L,

    val affiche_Dialog_Fast_Affiche_Panie: Boolean = if (M18CentralParametresOfAllApps().au_Lence_Set_Compt_Ac_KeyId
        ==M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId  )
        false else
        M18CentralParametresOfAllApps().itsDevMode,

    val startIntOffset_PresistantFABs: IntOffset =  IntOffset(650,-500),
    //-----------------Fast.PAnie-------------------------------------------------------------------------------------------------------------------------
    val activeFilters: Set<ActiveFilter> = emptySet(),

    val held_Produit_Pour_Move_Au_Position_Store: ArticlesBasesStatsTable? =null,
    val affiche_CheckList_ChoisiseurActiveFilter: Boolean = false,

    val sortVentsParClassment: Boolean = false,

    //-----------------Fabs.Affichage-------------------------------------------------------------------------------------------------------------------------
    val affiche_Floating_Button_TogleFilterMarquers: Boolean = false,
    val affiche_Floating_Button_Cible_Client: Boolean = false,

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
        ADD_Au_Ciblage_Clients(Color(0xFFFF5722)),
        Affiche_OnCommand_VentPeriod_Transaction(Color(0xFF9C27B0)),
    }

    sealed class RoleDefinieParSourceACetteFragment {
        data object AfficheSearchAllProduits : RoleDefinieParSourceACetteFragment()
        data class SearchProduit(val produit: ArticlesBasesStatsTable) :
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
    }
}
