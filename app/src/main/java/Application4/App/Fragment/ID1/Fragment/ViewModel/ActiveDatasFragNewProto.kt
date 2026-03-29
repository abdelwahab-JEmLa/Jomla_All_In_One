package Application4.App.Fragment.ID1.Fragment.ViewModel

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class ActiveDatasFragNewProto {

    // ── Raw stored values ─────────────────────────────────────────────────

    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
    var affiche_Dialog_Fast_Affiche_Panie: Boolean? by mutableStateOf(null)

    var section_ToggleButton_TagPrioriter__start_Collapsed: Boolean? by mutableStateOf(null)
    var filter_marqueClient_enum_entries: MapClientsViewModel.VisibleClientsNow? by mutableStateOf(null)

    var affiche_produits_Ou_On_TagPrioriter: Set<Prioriter>? by mutableStateOf(Prioriter.entries.toSet())

    var list_M1Produit: List<M01Produit>? by mutableStateOf(null)
    var list_M03CouleurProduitInfos: List<M3CouleurProduitInfos>? by mutableStateOf(null)

    var list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur:
            List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>
            by mutableStateOf(emptyList())
    var listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state: List<M10OperationVentCouleur>? by mutableStateOf(null)
    var active_M21Catalogue: M21CataloguesCategorie by mutableStateOf(
        get_ListM21CataloguesCategorie().find { it.keyID == "t1" } ?: M21CataloguesCategorie()
    )
    var listM16_FilteredBy_active_M21Catalogue: List<M16CategorieProduit>? by mutableStateOf(null)
    var lastKnownBonVentKey: String? = null


    var list_M8BonVent: List<M8BonVent>? by mutableStateOf(null)
    var list_M2Client: List<M2Client>? by mutableStateOf(null)

    val activeOnVent_M8BonVent: M8BonVent? by derivedStateOf {
        active_M9Compt?.onVentM8BonVentKey
            ?.takeIf { it.isNotBlank() && it != "null" }
            ?.let { key -> list_M8BonVent?.find { it.keyID == key } }
    }

    val onVentList: List<M10OperationVentCouleur> by derivedStateOf {
        active_M9Compt?.onVentM8BonVentKey
            ?.takeIf { it.isNotBlank() && it != "null" }
            ?.let { key ->
                listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                    ?.filter { it.parent_M8BonVent_KeyId == key }
            } ?: emptyList()
    }

    val activeOnVent_M2Client: M2Client? by derivedStateOf {
        activeOnVent_M8BonVent?.parent_M2Client_KeyID
            ?.let { clientKey -> list_M2Client?.find { it.keyID == clientKey } }
    }

    val filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod: List<M8BonVent> by derivedStateOf {
        val periodKey = active_M9Compt?.current_OnVent_M14VentPeriode_KeyID ?: ""
        if (periodKey.isBlank()) emptyList()
        else list_M8BonVent?.filter { it.parent_M14VentPeriod_KeyId == periodKey } ?: emptyList()
    }

    val currentApp_Est_Admin: Boolean by derivedStateOf {
        active_M9Compt?.its_Admin == true
    }

    val currentApp_ItsWorkChezGrossisst: Boolean by derivedStateOf {
        active_M9Compt?.travailleChezGrossisst3Ali == true
    }
}
