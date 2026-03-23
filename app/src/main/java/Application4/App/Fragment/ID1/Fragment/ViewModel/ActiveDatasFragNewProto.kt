package Application4.App.Fragment.ID1.Fragment.ViewModel

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import EntreApps.Shared.Modules.Base.SQL.Dao_M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.Dao_M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ActiveDatasFragNewProto8 {
    var active_M9Compt: Z_AppCompt?  by mutableStateOf(null)

    var list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur: List<List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>>
            by mutableStateOf(emptyList())

    var listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state: List<M10OperationVentCouleur>?
            by mutableStateOf(null)

    var active_M21Catalogue: M21CataloguesCategorie
            by mutableStateOf(get_ListM21CataloguesCategorie().find { it.keyID == "t1" }
                ?: M21CataloguesCategorie())

    var affiche_produits_Ou_On_TagPrioriter: Set<Prioriter>? by mutableStateOf(
        Prioriter.entries.toSet()
    )

    var listM16_FilteredBy_active_M21Catalogue: List<M16CategorieProduit>?
            by mutableStateOf(null)

    var lastKnownBonVentKey: String? = null

    fun get_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
        dao_M9AppCompt: Dao_M9AppCompt,
    ): Flow<Z_AppCompt?> =
        getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(dao_M9AppCompt)
            .onEach { compt -> active_M9Compt = compt }

    companion object {
      /*  @OptIn(ExperimentalCoroutinesApi::class)
        fun getFlow_list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur(
            dao_M16CategorieProduit: Dao_M16CategorieProduit,
            allCatalogues: List<M21CataloguesCategorie>,
            allProducts: List<M01Produit>,
            allColours: List<M3CouleurProduitInfos>,
            activeDatasFragNewProto: ActiveDatasFragNewProto,
        ): Flow<List<List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>>> {
            val activeFilter = activeDatasFragNewProto.affiche_produits_Ou_On_TagPrioriter

            return dao_M16CategorieProduit.getAllFlow().map { allCategories ->
                allCatalogues.map { catalogue ->
                    val categoriesForCatalogue = allCategories
                        .filter { it.catalogueParentId == catalogue.id }

                    val categoryProductColourTree =
                        categoriesForCatalogue.map { category ->
                            val productsForCategory = allProducts
                                .filter { it.idParentCategorie == category.id }
                                .filter { it.matchesPrioriteFilter(activeFilter) }

                            val productColourPairs = productsForCategory.map { product ->
                                val coloursForProduct = allColours
                                    .filter { it.parentBProduitInfosKeyID == product.keyID }
                                product to coloursForProduct
                            }
                            category to productColourPairs
                        }

                    listOf(catalogue to categoryProductColourTree)
                }
            }
        }          */

        @OptIn(ExperimentalCoroutinesApi::class)
        fun getFlow_listM10OperationVentCouleur_By_active_Central_Values(
            dao_M10OperationVentCouleur: Dao_M10OperationVentCouleur,
            dao_M9AppCompt: Dao_M9AppCompt,
        ): Flow<Pair<String?, List<M10OperationVentCouleur>>> =
            dao_M9AppCompt.getFlow_ByKeyID(
                M18CentralParametresOfAllApps.Companion.get_Default().au_Lence_Set_Compt_Ac_KeyId
            ).flatMapLatest { activeCompt ->
                val onVentKey = activeCompt?.onVentM8BonVentKey
                    ?.takeIf { it.isNotBlank() && it != "null" }

                if (onVentKey == null) flowOf(null to emptyList())
                else dao_M10OperationVentCouleur
                    .getFlow_ListM10OperationVentCouleur_Of_Active_M8Bon_Key(onVentKey)
                    .map { list ->
                        val filtered = list.filter { it.parent_M8BonVent_KeyId == onVentKey }
                        onVentKey to filtered
                    }
            }

        fun getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
            dao_M9AppCompt: Dao_M9AppCompt,
        ): Flow<Z_AppCompt?> =
            dao_M9AppCompt.getFlow_ByKeyID(
                M18CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            )

        fun getFlow_listM16_FilteredBy_active_M21Catalogue(
            dao_M16CategorieProduit: Dao_M16CategorieProduit,
            active_M21Catalogue: M21CataloguesCategorie,
        ): Flow<List<M16CategorieProduit>> =
            dao_M16CategorieProduit.getAllFlow()
                .map { list -> list.filter { it.catalogueParentId == active_M21Catalogue.id } }
    }
}

enum class Prioriter {
    Dernier_VentAchat_Est_Trop_Luin,
    Dernier_VentAchat_Est_Moin_Mois,
    Dernier_VentAchat_Est_Moin_Semain,
    PlusDe80P_Ne_Le_Voit_Pas,
}




