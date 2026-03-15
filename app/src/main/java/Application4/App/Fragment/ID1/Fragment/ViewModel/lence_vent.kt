package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View.DepotUpdateResult
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import kotlinx.coroutines.flow.update

fun return_newlistM10_et_newUpdateCouleurCount(
    viewModel_NewProtoPatterns: ViewModel_NewProtoPatterns,
    operation: M10OperationVentCouleur,
    couleur: M3CouleurProduitInfos,
    isGrossist: Boolean,
    previousQuantity: Int,
    onDepotUpdateFailed: (DepotUpdateResult) -> Unit,
): Pair<List<M10OperationVentCouleur>?, M3CouleurProduitInfos?> {  // <-- fixed return type
    val isNew = operation.keyID.isEmpty() || operation.keyID == "null"
    val quantityChange = if (isNew) operation.quantity else operation.quantity - previousQuantity

    viewModel_NewProtoPatterns._uiStateNewProtoPatterns.update { state ->
        val current = state.list_Datas ?: List_Datas()
        val updatedOps = if (isNew) current.m10OperationVentCouleur + operation
        else current.m10OperationVentCouleur.map { if (it.keyID == operation.keyID) operation else it }
        state.copy(list_Datas = current.copy(m10OperationVentCouleur = updatedOps))
    }

    val activeBonVentKeyID = viewModel_NewProtoPatterns._uiStateNewProtoPatterns.value
        .active_Central_Values.activeOnVent_M8BonVent?.keyID

    var updatedFilteredList: List<M10OperationVentCouleur>? = null

    if (operation.parent_M8BonVent_KeyId == activeBonVentKeyID) {
        val activeDatas = viewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.active_Datas
        val currentFiltered = activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent ?: emptyList()
        updatedFilteredList = if (isNew) currentFiltered + operation
        else currentFiltered.map { if (it.keyID == operation.keyID) operation else it }
        activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent = updatedFilteredList
    }

    if (isGrossist || quantityChange == 0) return Pair(updatedFilteredList, null)  // <-- updated

    val currentCouleur = viewModel_NewProtoPatterns._uiStateNewProtoPatterns.value
        .list_Datas?.m3CouleurProduit?.find { it.keyID == couleur.keyID } ?: couleur
    val newCount = currentCouleur.count_Don_Depot - quantityChange

    if (newCount < 0) {
        onDepotUpdateFailed(DepotUpdateResult(
            success = false,
            message = "Stock insuffisant au dépôt",
            currentCount = currentCouleur.count_Don_Depot,
            requestedChange = -quantityChange
        ))
        return Pair(updatedFilteredList, null)  // <-- updated
    }

    val updatedCouleur = currentCouleur.copy(count_Don_Depot = newCount)  // <-- NEW: build updated couleur

    return Pair(updatedFilteredList, updatedCouleur)  // <-- updated: return both
}
