package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.Model

data class FilterState_Facad_Boutique_FragId5(
    val produit_a_Une_Couleur_Ac_Image: WhatDo = WhatDo.N_Affiche_Que_Lui,
    val hide_non_couleurAuDepot: Boolean = true,

    val prioritiseProduitsEnVente: Boolean = false,

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
    PRIX_ACHAT_TIME_DESC,
    PRIX_ACHAT_TIME_ASC
}
