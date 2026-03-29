package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.Model.FilterState_Facad_Boutique_FragId5
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * FilterTunnel - Extracted filtering logic
 * FIXED: Now a regular function (not @Composable) since it only does data filtering
 * Handles all filtering operations including the new image-based filter
 */
 fun FilterTunnel(
    groupe_Par_Catalogue: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>>,
    catalogueFilter: String?,
    filterState: FilterState_Facad_Boutique_FragId5
): List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> {
    fun matchesCatalogue(catalogue: M21CataloguesCategorie, filter: String): Boolean {
        return catalogue.keyID == filter
    }

    fun hasImageFile(couleur: M3CouleurProduitInfos): Boolean {
        if (couleur.nomImageFichieSansEtansion == "Non Dispo") return false

        val fileName = "${couleur.nomImageFichieSansEtansion}.${couleur.extensionDisponible}"
        val imageFile = File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
        return imageFile.exists()
    }

    fun matchesFilters(
        product: M01Produit,
        colors: List<M3CouleurProduitInfos>
    ): Boolean {
        // Search text filter
        if (filterState.searchText.isNotEmpty()) {
            if (!product.nom.contains(filterState.searchText, ignoreCase = true) &&
                !product.nomArab.contains(filterState.searchText, ignoreCase = true) &&
                !product.autreNomDarticle?.contains(filterState.searchText, ignoreCase = true)!!
            ) return false
        }

        // Depot filter
        if (filterState.hide_non_couleurAuDepot && colors.none { it.count_Don_Depot > 0 }) return false

        // Image-based filter
        when (filterState.produit_a_Une_Couleur_Ac_Image) {
            FilterState_Facad_Boutique_FragId5.WhatDo.N_Affiche_Que_Lui -> {
                // Only show products that have at least one color with an image
                if (colors.none { hasImageFile(it) }) return false
            }
            FilterState_Facad_Boutique_FragId5.WhatDo.Ne_Affiche_Aucune -> {
                // Don't show products that have any color with an image
                if (colors.any { hasImageFile(it) }) return false
            }
            FilterState_Facad_Boutique_FragId5.WhatDo.Ignore -> {
                // Do nothing - ignore this filter
            }
        }

        // Other product filters
        if (filterState.hideQuiNeSontPas_cUnNeveauArrivage && !product.itsNewArrivale) return false
        if (filterState.hidePetiteProbability && product.disponibilityEtates == DisponibilityEtates.PETITE_PROBABILITY) return false
        if (filterState.hidePrixAchatZero && product.prixAchat <= 0.0) return false
        if (filterState.hidePrixAchatPositif && product.prixAchat > 0.0) return false
        if (filterState.hidePrixVenteZero && product.prixVent <= 0.0) return false
        if (filterState.hidePrixVentePositif && product.prixVent > 0.0) return false
        if (filterState.hideHeldPrioriteDemandAuGrossist && product.heldPrioriteDemandAuGrossist) return false
        if (filterState.hideNonHeldPrioriteDemandAuGrossist && !product.heldPrioriteDemandAuGrossist) return false

        // Time filter
        if (filterState.enablePrixAchatTimeFilter && filterState.prixAchatTimeFilterDays.isNotEmpty()) {
            filterState.prixAchatTimeFilterDays.toIntOrNull()?.let { days ->
                if (days > 0) {
                    val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong())
                    if (product.prixAchatDernierTimeTempUpdate < cutoff) return false
                }
            }
        }

        return true
    }

    // Apply catalogue filter
    val afterCatalogue = if (!catalogueFilter.isNullOrEmpty()) {
        groupe_Par_Catalogue.filter { (catalogue, _) ->
            matchesCatalogue(catalogue, catalogueFilter)
        }
    } else {
        groupe_Par_Catalogue
    }

    // Filter products within catalogues and categories
    return afterCatalogue.mapNotNull { (catalogue, categoriesWithProducts) ->
        val filteredCategories = categoriesWithProducts
            .map { (category, products) ->
                category to products.filter { (product, colors) ->
                    matchesFilters(product, colors)
                }
            }
            .filter { (_, products) -> products.isNotEmpty() }

        if (filteredCategories.isNotEmpty()) {
            catalogue to filteredCategories
        } else {
            null
        }
    }
}
