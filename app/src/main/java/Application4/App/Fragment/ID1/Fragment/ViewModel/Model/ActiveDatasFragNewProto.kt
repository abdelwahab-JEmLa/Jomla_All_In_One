package Application4.App.Fragment.ID1.Fragment.ViewModel.Model

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Prioriter
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

@Stable
class ActiveDatasFragNewProto {
    var active_M9Compt: Z_AppCompt? by mutableStateOf(null)
    var list_M1Produit: List<M01Produit>? by mutableStateOf(null)
    var list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur:
            List<List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>>
            by mutableStateOf(emptyList())
    var listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state: List<M10OperationVentCouleur>? by mutableStateOf(null)
    var active_M21Catalogue: M21CataloguesCategorie by mutableStateOf(
        get_ListM21CataloguesCategorie().find { it.keyID == "t1" } ?: M21CataloguesCategorie()
    )
    var affiche_produits_Ou_On_TagPrioriter: Set<Prioriter>? by mutableStateOf(Prioriter.entries.toSet())
    var listM16_FilteredBy_active_M21Catalogue: List<M16CategorieProduit>? by mutableStateOf(null)
    var lastKnownBonVentKey: String? = null

    fun get_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(dao_M9AppCompt: Dao_M9AppCompt): Flow<Z_AppCompt?> =
        ActiveDatasFragNewProtoFlows.getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(dao_M9AppCompt)
            .onEach { compt -> active_M9Compt = compt }
}
