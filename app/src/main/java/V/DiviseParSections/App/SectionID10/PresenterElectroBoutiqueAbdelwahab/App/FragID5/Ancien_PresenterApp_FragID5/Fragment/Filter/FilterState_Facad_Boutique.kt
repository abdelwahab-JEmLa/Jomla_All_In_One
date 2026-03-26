package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter

/**
 * FilterState_Facad_Boutique - State for filtering products in the boutique facade
 *
 * FIXED: Added clear documentation for produit_a_Une_Couleur_Ac_Image filter behavior
 */
data class FilterState_Facad_Boutique_FragId5(
    /**
     * Filter products based on whether they have colors with images
     *
     * Behavior:
     * - N_Affiche_Que_Lui: Only display products that have at least one color with an existing image file
     * - Ne_Affiche_Aucune: Only display products that have NO colors with images (exclude all products with images)
     * - Ignore: Don't apply this filter (show all products regardless of image availability)
     *
     * Image existence is determined by checking if the file exists at:
     * /storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/{nomImageFichieSansEtansion}.{extensionDisponible}
     */
    val produit_a_Une_Couleur_Ac_Image: WhatDo = WhatDo.N_Affiche_Que_Lui ,
    val hide_non_couleurAuDepot: Boolean = true,

    val affiche_dialog_editeur: Boolean = false,

    val hide_header_categorie: Boolean = false,

    val hideQuiNeSontPas_cUnNeveauArrivage: Boolean = false,
    val hidePetiteProbability: Boolean = false,

    val hidePrixAchatZero: Boolean = false,
    val hidePrixAchatPositif: Boolean = false,
    val hidePrixVenteZero: Boolean = false,
    val hidePrixVentePositif: Boolean = false,

    val hideHeldPrioriteDemandAuGrossist: Boolean = false,
    val hideNonHeldPrioriteDemandAuGrossist: Boolean = false,

    /**
     * When true, products that are currently in the active sale (on vent) are
     * floated to the very top of the list, regardless of the active sort order.
     */
    val prioritiseProduitsEnVente: Boolean = false,

    val searchText: String = "",
    val sortOrderFacadeBoutique: SortOrder_Facade_Boutique = SortOrder_Facade_Boutique.CATEGORY_GROUPED,
    val enableCategoryGrouping: Boolean = true,
    val prixAchatTimeFilterDays: String = "",
    val enablePrixAchatTimeFilter: Boolean = false,


    ) {
    enum class WhatDo {
        /** Only show products that have at least one color with an image */
        N_Affiche_Que_Lui,

        /** Only show products that have NO colors with images */
        Ne_Affiche_Aucune,

        /** Ignore this filter - show all products */
        Ignore,
    }
}

enum class SortOrder_Facade_Boutique {
    ID_DESC,
    ID_ASC,
    NAME_ASC,
    NAME_DESC,
    CATEGORY_GROUPED,
    PRIX_ACHAT_TIME_DESC,  // Most recently updated purchase prices first
    PRIX_ACHAT_TIME_ASC    // Oldest updated purchase prices first
}
