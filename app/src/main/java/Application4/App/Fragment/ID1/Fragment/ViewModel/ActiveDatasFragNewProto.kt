package Application4.App.Fragment.ID1.Fragment.ViewModel

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class ActiveDatasFragNewProto {
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
    var affiche_Dialog_Fast_Affiche_Panie: Boolean? by mutableStateOf(null)

    var section_ToggleButton_TagPrioriter__start_Collapsed: Boolean? by mutableStateOf(true)
    var filter_marqueClient_enum_entries: MapClientsViewModel.VisibleClientsNow? by mutableStateOf(
        null
    )

    var isEchatillantsMode: Boolean by mutableStateOf(false)
    var its_Panie_Mode: Boolean by mutableStateOf(false)

    var list_M1Produit: List<M01Produit>? by mutableStateOf(null)
    var list_M03CouleurProduitInfos: List<M3CouleurProduitInfos>? by mutableStateOf(null)
    var list_M16CategorieProduit: List<M16CategorieProduit>? by mutableStateOf(null)

    var parentProduit_Classement: Map<String, Int> by mutableStateOf(emptyMap())

    var listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state: List<M10OperationVentCouleur>? by mutableStateOf(
        null
    )
    var lastKnownBonVentKey: String? = null

    var list_M10OperationVentCouleur: List<M10OperationVentCouleur>? by mutableStateOf(null)

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

enum class Prioriter {
    Affiche_Que_Les_Produits_De_Jomla_Clients_ECHATILLANTS,
}
