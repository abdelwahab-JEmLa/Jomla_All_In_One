package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur

object ProductListFilterLogic {
    // ── Step 1 ───────────────────────────────────────────────────────────────
    fun filterByDepot(
        list: List<M3CouleurProduitInfos>,
    ): List<M3CouleurProduitInfos> =
        list.filter { it.count_Don_Depot > 0 }

    // ── Step 2 ───────────────────────────────────────────────────────────────
    fun filterByQuery(
        list: List<M3CouleurProduitInfos>,
        query: String,
    ): List<M3CouleurProduitInfos> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return list
        return list.filter {
            it.nomCouleurStrSiSonImageDispo.lowercase().contains(q) ||
                    it.keyID.lowercase().contains(q) ||
                    it.parentBProduitInfosKeyID.lowercase().contains(q) ||
                    it.parentId1ProduitInfosDebugName.lowercase().contains(q)
        }
    }

    // ── Step 3 ───────────────────────────────────────────────────────────────
    fun filterByMode(
        list: List<M3CouleurProduitInfos>,
        mode: Filter_Affichage_Mode_Proto,
        ventCouleurs: List<M10OperationVentCouleur>,
    ): List<M3CouleurProduitInfos> = when (mode) {
        Filter_Affichage_Mode_Proto.Tablette_Produits_Seulement ->
            list.filter { !it.its_in_echantiallants }
        Filter_Affichage_Mode_Proto.Echants_Seulement ->
            list.filter { it.its_in_echantiallants }
        Filter_Affichage_Mode_Proto.Tablette_Et_Echants -> list
        Filter_Affichage_Mode_Proto.Panie -> {
            val activeKeys = ventCouleurs
                .filter { it.quantity > 0 }
                .map { it.parent_M3CouleurProduit_KeyID }
                .toSet()
            list.filter { it.keyID in activeKeys }
        }
    }

    // ── Steps 4 & 5 ─────────────────────────────────────────────────────────
    fun groupAndSort(
        filteredColors: List<M3CouleurProduitInfos>,
        productMap: Map<String, M01Produit>,
        mode: Filter_Affichage_Mode_Proto,
        echantillantsPurchaseOrder: List<String>,
        classement: Map<String, Int>,
    ): List<Pair<M01Produit, List<M3CouleurProduitInfos>>> =
        filteredColors
            .groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                val product = productMap[productKeyID] ?: return@mapNotNull null
                val sortedColors = if (mode == Filter_Affichage_Mode_Proto.Echants_Seulement) {
                    colors.sortedBy { color ->
                        val idx = echantillantsPurchaseOrder.indexOf(color.keyID)
                        if (idx >= 0) idx else Int.MAX_VALUE
                    }
                } else colors
                product to sortedColors
            }
            .sortedBy { (product, _) -> classement[product.keyID] ?: Int.MAX_VALUE }

    // ── Main entry-point ─────────────────────────────────────────────────────
    fun compute(
        rawColors: List<M3CouleurProduitInfos>?,
        productMap: Map<String, M01Produit>,
        query: String,
        mode: Filter_Affichage_Mode_Proto,
        ventCouleurs: List<M10OperationVentCouleur>,
        echantillantsPurchaseOrder: List<String>,
        classement: Map<String, Int>,
    ): List<Pair<M01Produit, List<M3CouleurProduitInfos>>> {
        val filtered = rawColors
            ?.let { if (mode == Filter_Affichage_Mode_Proto.Panie) it else filterByDepot(it) }
            ?.let { filterByQuery(it, query) }
            ?.let { filterByMode(it, mode, ventCouleurs) }
            ?: return emptyList()

        return groupAndSort(filtered, productMap, mode, echantillantsPurchaseOrder, classement)
    }
}
