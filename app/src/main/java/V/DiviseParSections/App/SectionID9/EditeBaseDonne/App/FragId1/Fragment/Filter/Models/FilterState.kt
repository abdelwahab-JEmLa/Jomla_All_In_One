package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.Models

data class FilterState(
    val hideQuiNeSontPas_cUnNeveauArrivage: Boolean = false,     //<--
    //TODO(1): ajout ca au filterr et il filter les prodit ou !cUnNeveauArrivage 
    val hideNonDispo: Boolean = false,
    val hideDispoOnly: Boolean = false,
    val hidePetiteProbability: Boolean = false,
    val hidePrixAchatZero: Boolean = false,
    val hidePrixAchatPositif: Boolean = false,
    val hideHeldPrioriteDemandAuGrossist: Boolean = false,
    val hideNonHeldPrioriteDemandAuGrossist: Boolean = false,
    val searchText: String = "",
    val sortOrder: SortOrder = SortOrder.CATEGORY_GROUPED,
    val enableCategoryGrouping: Boolean = true
)
