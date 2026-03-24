package Application4.App.Fragment.ID1.Fragment.ViewModel.Model

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.SQL.Dao_M16CategorieProduit
import EntreApps.Shared.Modules.Base.SQL.Dao_M1Produit
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.Dao_M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

object ActiveDatasFragNewProtoFlows {

    fun get_list_M1Produit(
        dao_M1Produit: Dao_M1Produit,
        activeDatasFragNewProto: ActiveDatasFragNewProto,
    ): Flow<List<M01Produit>> =
        dao_M1Produit.getAllFlow().onEach { activeDatasFragNewProto.list_M1Produit = it }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFlow_list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur(
        dao_M16CategorieProduit: Dao_M16CategorieProduit,
        allCatalogues: List<M21CataloguesCategorie>,
        allColours: List<M3CouleurProduitInfos>,
        activeDatasFragNewProto: ActiveDatasFragNewProto,
    ): Flow<List<List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>>> {
        val activeFilter = activeDatasFragNewProto.affiche_produits_Ou_On_TagPrioriter

        return dao_M16CategorieProduit.getAllFlow().map { allCategories ->
            val allProducts = activeDatasFragNewProto.list_M1Produit

            allCatalogues.mapNotNull { catalogue ->
                val categoriesForCatalogue = allCategories.filter { it.catalogueParentId == catalogue.id }

                val categoryProductColourTree = categoriesForCatalogue.mapNotNull { category ->
                    val productsForCategory = allProducts
                        ?.filter { it.idParentCategorie == category.id }
                        ?.filter { it.matchesPrioriteFilter(activeFilter) }
                        ?: emptyList()

                    if (productsForCategory.isEmpty()) return@mapNotNull null

                    val productColourPairs = productsForCategory.map { product ->
                        product to allColours.filter { it.parentBProduitInfosKeyID == product.keyID }
                    }
                    category to productColourPairs
                }

                if (categoryProductColourTree.isEmpty()) return@mapNotNull null
                listOf(catalogue to categoryProductColourTree)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFlow_listM10OperationVentCouleur_By_active_Central_Values(
        dao_M10OperationVentCouleur: Dao_M10OperationVentCouleur,
        dao_M9AppCompt: Dao_M9AppCompt,
    ): Flow<Pair<String?, List<M10OperationVentCouleur>>> =
        dao_M9AppCompt.getFlow_ByKeyID(
            M18CentralParametresOfAllApps.Companion.get_Default().au_Lence_Set_Compt_Ac_KeyId
        ).flatMapLatest { activeCompt ->
            val onVentKey = activeCompt?.onVentM8BonVentKey?.takeIf { it.isNotBlank() && it != "null" }
            if (onVentKey == null) flowOf(null to emptyList())
            else dao_M10OperationVentCouleur
                .getFlow_ListM10OperationVentCouleur_Of_Active_M8Bon_Key(onVentKey)
                .map { list -> onVentKey to list.filter { it.parent_M8BonVent_KeyId == onVentKey } }
        }

    fun getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(dao_M9AppCompt: Dao_M9AppCompt): Flow<Z_AppCompt?> =
        dao_M9AppCompt.getFlow_ByKeyID(M18CentralParametresOfAllApps.Companion.get_Default().au_Lence_Set_Compt_Ac_KeyId)

    fun getFlow_listM16_FilteredBy_active_M21Catalogue(
        dao_M16CategorieProduit: Dao_M16CategorieProduit,
        active_M21Catalogue: M21CataloguesCategorie,
    ): Flow<List<M16CategorieProduit>> =
        dao_M16CategorieProduit.getAllFlow()
            .map { list -> list.filter { it.catalogueParentId == active_M21Catalogue.id } }
}
