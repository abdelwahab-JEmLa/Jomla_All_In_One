package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.List_Datas
import Application4.App.Fragment.ID1.Fragment.ViewModel.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.FastInit_Outlined_Int_Edite_Modulable_Proto3
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.update

fun return_newlistM10_et_newUpdateCouleurCount(
    viewModel_NewProtoPatterns: ViewModel_NewProtoPatterns,
    operation: M10OperationVentCouleur,
    couleur: M3CouleurProduitInfos,
    isGrossist: Boolean,
    previousQuantity: Int,
): Pair<List<M10OperationVentCouleur>?, M3CouleurProduitInfos?> {

    val isNew = operation.keyID.isEmpty() || operation.keyID == "null"
    val quantityChange = if (isNew) operation.quantity else operation.quantity - previousQuantity

    // 1. Update the global list in uiState
    viewModel_NewProtoPatterns._uiStateNewProtoPatterns.update { state ->
        val current = state.list_Datas ?: List_Datas()
        val updatedOps = if (isNew) current.m10OperationVentCouleur + operation
        else current.m10OperationVentCouleur.map { if (it.keyID == operation.keyID) operation else it }
        state.copy(list_Datas = current.copy(m10OperationVentCouleur = updatedOps))
    }

    // 2. Update the filtered list for the active BonVent
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

    // 3. Grossist or no quantity delta → no depot update needed
    if (isGrossist || quantityChange == 0) return Pair(updatedFilteredList, null)

    // 4. Resolve the current couleur from state (may be fresher than the parameter)
    val currentCouleur = viewModel_NewProtoPatterns._uiStateNewProtoPatterns.value
        .list_Datas?.m3CouleurProduit?.find { it.keyID == couleur.keyID } ?: couleur

    val newCount = currentCouleur.count_Don_Depot - quantityChange
    val updatedCouleur = currentCouleur.copy(count_Don_Depot = newCount)

    return Pair(updatedFilteredList, updatedCouleur)
}

@Composable
fun Lenceur_Vent_Handler_FragID3(
    relative_M1produit: M01Produit,
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    selectedCouleur: M3CouleurProduitInfos,
    selectedTariff: M13TarificationInfos,
    compactMode: Boolean = false,
    modifier: Modifier = Modifier,
    isWifiClientConnected: Boolean = false,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val centralValues = uiState.active_Central_Values

    val isGrossist = centralValues.activeCompt?.travailleChezGrossisst3Ali == true
    val isAdmin = centralValues.currentApp_Est_Admin
    val activeOnVent_M8BonVent = centralValues.activeOnVent_M8BonVent

    var depotAlertInfo by remember { mutableStateOf<DepotUpdateResult?>(null) }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // Depot count observed from uiState list — recomposes automatically on data change
    val au_depot by remember(selectedCouleur.keyID, uiState.list_M3CouleurProduit) {
        derivedStateOf {
            uiState.list_M3CouleurProduit
                .find { it.keyID == selectedCouleur.keyID }
                ?.count_Don_Depot ?: selectedCouleur.count_Don_Depot
        }
    }

    val currentQuantity by remember(
        relative_M10OperationVentCouleur?.keyID,
        relative_M10OperationVentCouleur?.quantity
    ) {
        derivedStateOf {
            relative_M10OperationVentCouleur?.quantity ?: 0
        }
    }

    val standardCount = remember(relative_M1produit.setIN_Vent_Its_Quantity_Represent) {
        if (relative_M1produit.setIN_Vent_Its_Quantity_Represent ==
            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
        )
            relative_M1produit.quantite_Boit_Par_Carton
        else
            1
    }

    val isAvailable by remember(au_depot, isGrossist) {
        derivedStateOf { au_depot > 0 || isGrossist }
    }

    fun handleDepotUpdate(newDepotCount: Int) {
        viewModel.update_depot_count(
            couleur = selectedCouleur,
            newDepotCount = newDepotCount,
            onSuccess = {
                Toast.makeText(
                    context,
                    "✓ Dépôt mis à jour: $newDepotCount unité(s)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    var  quantity  by remember { mutableStateOf(1) }

    val previousQuantity = currentQuantity
    val operationToUse = relative_M10OperationVentCouleur?.copy(
        quantity = quantity,
        parentM13TarificationKeyID = selectedTariff.keyID,
        parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
        typeTarificationEnumT2 = selectedTariff.typeChoisi,
        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
    ) ?: M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
        activeOnVent_M8BonVent,
        selectedCouleur
    ).copy(
        creationTimestamps = System.currentTimeMillis(),
        setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
        quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
        quantity = quantity,
        parentM13TarificationKeyID = selectedTariff.keyID,
        parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
        typeTarificationEnumT2 = selectedTariff.typeChoisi,
        its_created_in_working_for_wholesaler = isGrossist
    )
    val result_newlistM10_et_newUpdateCouleurCount = return_newlistM10_et_newUpdateCouleurCount(
        viewModel_NewProtoPatterns = viewModel,
        operation = operationToUse,
        couleur = selectedCouleur,
        isGrossist = isGrossist,
        previousQuantity = previousQuantity,
    )

    val (updatedFilteredList, updatedCouleur) = result_newlistM10_et_newUpdateCouleurCount
    fun handleLenceVent() {
        viewModel.update_listM10OperationVentCouleur_FilteredBy_activeM8BonVent(updatedFilteredList)
        if (updatedCouleur != null) viewModel.update_m3couleur(updatedCouleur)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val horizontalPadding = if (compactMode) 4.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp

    val shape = RoundedCornerShape(
        topStart = 0.dp, topEnd = 0.dp,
        bottomStart = 12.dp, bottomEnd = 12.dp
    )

    val containerColor = if (!isAvailable) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    } else if (currentQuantity > 0) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = uiState.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent,
                    key = SemanticsPropertyKey("listM10OperationVentCouleur_FilteredBy_activeM8BonVent"))
            }
            .semantics(mergeDescendants = true) {
                set(value = updatedFilteredList, key = SemanticsPropertyKey("updatedFilteredList"))
            }
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor.copy(alpha = 0.15f))
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.CenterEnd
    ) {
        FastInit_Outlined_Int_Edite_Modulable_Proto3(
            start_count = currentQuantity,
            au_depot = au_depot,
            standard_count = standardCount,
            start_au_premier_click_par_add_outlined = false,
            icon = Icons.Default.ShoppingCart,
            isAvailable = isAvailable,
            compact_taille = compactMode,
            show_depot_card_on_top_in_flow_row = true,
            is_admin = isAdmin,
            add_spacing_between_depot_and_sale = isAdmin,
            on_admin_depot_update = { newDepotCount -> handleDepotUpdate(newDepotCount) }
        ) { newQuantity ->
            quantity=newQuantity
            handleLenceVent()
        }
    }

    depotAlertInfo?.let { alertInfo ->
        DepotAlertDialog(
            alertInfo = alertInfo,
            onDismiss = { depotAlertInfo = null }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DepotUpdateResult
// ─────────────────────────────────────────────────────────────────────────────
data class DepotUpdateResult(
    val success: Boolean,
    val message: String = "",
    val currentCount: Int = 0,
    val requestedChange: Int = 0
)
