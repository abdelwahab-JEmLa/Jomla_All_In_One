package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode

object ProductListFilterLogic {

    // ── Step 1 ───────────────────────────────────────────────────────────────
    fun filterByDepot(
        list: List<M3CouleurProduitInfos>,
    ): List<M3CouleurProduitInfos> {
        val counts = list.associate { it.keyID to it.count_Don_Depot }
        return list.filter {
            if (it.c_unite_couleur_de_couleurKey.isNotEmpty()) {
                (counts[it.c_unite_couleur_de_couleurKey] ?: 0) > 0
            } else {
                it.count_Don_Depot > 0
            }
        }
    }

    // ── Step 2 ───────────────────────────────────────────────────────────────
    fun filterByQuery(
        list: List<M3CouleurProduitInfos>,
        query: String,
        productMap: Map<String, M01Produit> = emptyMap(),
    ): List<M3CouleurProduitInfos> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return list       // 0 lettres → tout afficher (normal)
        if (q.length < 3) return emptyList() // 1-2 lettres → rien afficher
        return list.filter {
            val productNom = productMap[it.parentBProduitInfosKeyID]?.nom?.lowercase() ?: ""
            productNom.contains(q) ||
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
        Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres -> {
            val activeColorKeys = ventCouleurs
                .map { it.parent_M3CouleurProduit_KeyID }
                .toSet()
            val activeParentKeys = list
                .filter { it.keyID in activeColorKeys }
                .map { it.parentBProduitInfosKeyID }
                .toSet()
            list.filter { it.parentBProduitInfosKeyID in activeParentKeys }
        }
    }

    enum class Sort_Order {
        Produits_Grouped_Par_Categories,
        Vents_Creation,
    }

    // ── Steps 4 & 5 ─────────────────────────────────────────────────────────
    fun groupAndSort(
        sort_Order: Sort_Order = Sort_Order.Produits_Grouped_Par_Categories,
        filteredColors: List<M3CouleurProduitInfos>,
        productMap: Map<String, M01Produit>,
        mode: Filter_Affichage_Mode_Proto,
        echantillantsPurchaseOrder: List<String>,
        classement: Map<String, Int>,
        ventCouleurs: List<M10OperationVentCouleur> = emptyList(),
        categories: List<M16CategorieProduit> = emptyList(),
        catalogues: List<M21CataloguesCategorie> = emptyList(),
    ): List<Pair<M01Produit, List<M3CouleurProduitInfos>>> {
        val pairs = filteredColors
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

        return when (sort_Order) {
            Sort_Order.Produits_Grouped_Par_Categories -> {
                val categoryMap  = categories.associateBy { it.id }
                val catalogueMap = catalogues.associateBy { it.id }
                pairs.sortedWith(
                    compareBy(
                        { (product, _) ->
                            val cat = categoryMap[product.idParentCategorie]
                            catalogueMap[cat?.catalogueParentId]?.position ?: Int.MAX_VALUE
                        },
                        { (product, _) ->
                            categoryMap[product.idParentCategorie]?.positionDouble ?: Double.MAX_VALUE
                        },
                        { (product, _) -> classement[product.keyID] ?: Int.MAX_VALUE }
                    )
                )
            }
            Sort_Order.Vents_Creation -> {
                val latestVentTimestampByColorKey = ventCouleurs
                    .groupBy { it.parent_M3CouleurProduit_KeyID }
                    .mapValues { (_, vents) -> vents.maxOf { it.creationTimestamps } }
                pairs.sortedByDescending { (_, colors) ->
                    colors.maxOfOrNull { latestVentTimestampByColorKey[it.keyID] ?: 0L } ?: 0L
                }
            }
        }
    }

    fun filterLinkedColors(
        list: List<M3CouleurProduitInfos>,
        mode: Filter_Affichage_Mode_Proto,
    ): List<M3CouleurProduitInfos> {
        if (mode == Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres) {
            return list
        }
        val counts = list.associate { it.keyID to it.count_Don_Depot }
        return list.filter {
            if (it.c_unite_couleur_de_couleurKey.isNotEmpty()) {
                (counts[it.c_unite_couleur_de_couleurKey] ?: 0) > 0
            } else {
                true
            }
        }
    }

    // ── Main entry-point ─────────────────────────────────────────────────────
    fun compute(
        rawColors: List<M3CouleurProduitInfos>?,
        productMap: Map<String, M01Produit>,
        query: String,
        mode: Filter_Affichage_Mode_Proto,
        ventCouleurs: List<M10OperationVentCouleur>,
        categories: List<M16CategorieProduit> = emptyList(),
        catalogues: List<M21CataloguesCategorie> = emptyList(),
        echantillantsPurchaseOrder: List<String>,
        periode: M14VentPeriode?=null,
        classement: Map<String, Int>,
        sort_Order: Sort_Order = Sort_Order.Produits_Grouped_Par_Categories,
    ): List<Pair<M01Produit, List<M3CouleurProduitInfos>>> {
        // When the user is actively searching in Panie+frères mode, don't restrict the results
        // to siblings of sold items — let the search query do the filtering instead.
        val skipModeFilter =
            mode == Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres &&
                    query.trim().isNotEmpty()

        val cleanColors = rawColors?.let { filterLinkedColors(it, mode) }

        val filtered = cleanColors
            ?.let { filterByQuery(it, query, productMap) }
            ?.let { if (skipModeFilter) it else filterByMode(it, mode, ventCouleurs) }
            ?: return emptyList()

        return groupAndSort(
            sort_Order = sort_Order,
            filteredColors = filtered,
            productMap = productMap,
            mode = mode,
            echantillantsPurchaseOrder = echantillantsPurchaseOrder,
            classement = classement,
            ventCouleurs = ventCouleurs,
            categories = categories,
            catalogues = catalogues,
        )
    }
}
