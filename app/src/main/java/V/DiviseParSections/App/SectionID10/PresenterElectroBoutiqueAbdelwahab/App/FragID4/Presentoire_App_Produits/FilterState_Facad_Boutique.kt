package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits

data class FilterState_Facad_Boutique(
    val affiche_dialog_editeur: Boolean = false,
    val hide_non_couleurAuDepot: Boolean = false,
    
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
    val enablePrixAchatTimeFilter: Boolean = false
)

enum class SortOrder_Facade_Boutique {
    ID_DESC,
    ID_ASC,
    NAME_ASC,
    NAME_DESC,
    CATEGORY_GROUPED,
    PRIX_ACHAT_TIME_DESC,  // Most recently updated purchase prices first
    PRIX_ACHAT_TIME_ASC    // Oldest updated purchase prices first
}
