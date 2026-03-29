package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.List_Datas
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M09AppCompt
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.util.Log
import kotlinx.coroutines.flow.update

private const val TAG_SETTER = "Setter_ViewModel"

class Setter_ViewModel_NewProtoPatterns(private val vm: A_ViewModel_NewProtoPatterns) {
    // M01 ─────────────────────────────────────────────────────────────────
    fun update_m1Produit(new: M01Produit) {
        vm.active_Datas.list_M1Produit =
            vm.active_Datas.list_M1Produit?.map { if (it.keyID == new.keyID) new else it }
        vm.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur =
            vm.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur
                .map { (catalogue, categoryList) ->
                    catalogue to categoryList.map { (category, productList) ->
                        category to productList.map { (product, colours) ->
                            if (product.keyID == new.keyID) new to colours else product to colours
                        }
                    }
                }
        vm.repositorysMainSetter_NewProtoPatterns.update_M1Produit(new)
    }

    fun delete_m1Produit(produit: M01Produit) {
        vm.active_Datas.list_M1Produit =
            vm.active_Datas.list_M1Produit?.filter { it.keyID != produit.keyID }
        vm.repositorysMainSetter_NewProtoPatterns.delete_M1Produit(produit)
    }

    // M03 ─────────────────────────────────────────────────────────────────
    fun deleteInsertFireBase_listKeys_M3CouleurProduitInfos(
        keys: Map<String, Boolean>,
        onSuccess: () -> Unit = {}
    ) {
        vm.repositorysMainSetter_NewProtoPatterns
            .deleteInsertFireBase_listKeys_M3CouleurProduitInfos(keys, onSuccess)
    }

    fun update_m3couleur(couleur: M3CouleurProduitInfos) {
        vm.active_Datas.list_M03CouleurProduitInfos =
            vm.active_Datas.list_M03CouleurProduitInfos?.map { if (it.keyID == couleur.keyID) couleur else it }
        vm.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur =
            vm.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur
                .map { (catalogue, categoryList) ->
                    catalogue to categoryList.map { (category, productList) ->
                        category to productList.map { (product, colours) ->
                            product to colours.map { if (it.keyID == couleur.keyID) couleur else it }
                        }
                    }
                }
        vm.repositorysMainSetter_NewProtoPatterns.update_M3CouleurProduitInfos(couleur)
    }

    fun update_depot_count(
        couleur: M3CouleurProduitInfos,
        newDepotCount: Int,
        onSuccess: () -> Unit = {},
    ) {
        val updated = couleur.copy(
            count_Don_Depot = newDepotCount,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        vm.active_Datas.list_M03CouleurProduitInfos =
            vm.active_Datas.list_M03CouleurProduitInfos?.map { if (it.keyID == updated.keyID) updated else it }
        vm.repositorysMainSetter_NewProtoPatterns.update_M3CouleurProduitInfos(
            data = updated,
            onSuccess = onSuccess
        )
    }

    // M09 ─────────────────────────────────────────────────────────────────

    fun update_active_Compt(compt: M09AppCompt) {
        Log.d(TAG_SETTER, "update_active_Compt — keyID=${compt.keyID} | affiche=${compt.affiche_ProduitDataBaseEdites_ComposableViews}")
        vm.active_Datas.active_M9Compt = compt
        Log.d(TAG_SETTER, "  active_Datas.active_M9Compt written ✓ → launching Room+Firebase persist")
        vm.repositorysMainSetter_NewProtoPatterns.update_M9AppCompt(compt) {
            Log.d(TAG_SETTER, "  Room+Firebase persist completed ✓ for keyID=${compt.keyID}")
        }
    }

    // M10 ─────────────────────────────────────────────────────────────────

    fun update_listM10OperationVentCouleur_FilteredBy_activeM8BonVent(
        updatedList: List<M10OperationVentCouleur>?,
    ) {
        vm.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state = updatedList
        upsert_M10OperationVentCouleur(updatedList)
    }

    private fun upsert_M10OperationVentCouleur(updatedList: List<M10OperationVentCouleur>?) {
        val allTariffs = vm._uiStateNewProtoPatterns.value.list_Datas?.m13TarificationInfos
        updatedList?.forEach { operation ->
            val tariff = allTariffs?.find { it.keyID == operation.parentM13TarificationKeyID }
                ?: allTariffs?.filter { it.parent_M1Produit_KeyId == operation.parent_M1Produit_KeyId }
                    ?.maxByOrNull { it.creationTimestamps }
            if (tariff == null) return@forEach
            val opToSave = if (tariff.keyID != operation.parentM13TarificationKeyID) {
                operation.copy(
                    parentM13TarificationKeyID = tariff.keyID,
                    parentM13TarificationDebugInfos = tariff.getDebugInfos()
                )
            } else operation
            vm.repositorysMainSetter_NewProtoPatterns.upsert_M10OperationVentCouleur(opToSave, tariff)
        }
    }

    // M13 ─────────────────────────────────────────────────────────────────

    fun update_M13TarificationInfos(tariff: M13TarificationInfos) {
        vm._uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(
                list_Datas = current.copy(
                    m13TarificationInfos = current.m13TarificationInfos
                        .filter { it.keyID != tariff.keyID } + tariff
                )
            )
        }
        vm.repositorysMainSetter_NewProtoPatterns.update_M13TarificationInfos(tariff)
    }

    // M16 ─────────────────────────────────────────────────────────────────

    fun insert_M16CategorieProduit(new: M16CategorieProduit) {
        vm._uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(list_Datas = current.copy(m16CategorieProduit = current.m16CategorieProduit + new))
        }
        if (new.catalogueParentId == vm.active_Datas.active_M21Catalogue.id) {
            vm.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur =
                vm.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur
                    .map { (catalogue, categoryList) ->
                        catalogue to (categoryList + (new to emptyList()))
                    }
        }
        vm.repositorysMainSetter_NewProtoPatterns.insert_M16CategorieProduit(new)
    }

    fun update_m16CategorieProduit(new: M16CategorieProduit) {
        vm._uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(
                list_Datas = current.copy(
                    m16CategorieProduit = current.m16CategorieProduit.map {
                        if (it.keyID == new.keyID) new else it
                    }
                )
            )
        }
        vm.repositorysMainSetter_NewProtoPatterns.update_M16CategorieProduit(new)
    }
}
