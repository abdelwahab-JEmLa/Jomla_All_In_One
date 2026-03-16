package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.FastInit_Outlined_Int_Edite_Modulable_Proto3
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.util.Log
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
import androidx.compose.runtime.SideEffect
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

@Composable
fun Lenceur_Vent_Handler_FragID3(
    relative_M1produit: M01Produit,
    selectedCouleur: M3CouleurProduitInfos,
    selectedTariff: M13TarificationInfos,
    compactMode: Boolean = false,
    modifier: Modifier = Modifier,
    isWifiClientConnected: Boolean = false,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>
) {

    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val centralValues = uiState.active_Central_Values
    val listM10OperationVentCouleur_FilteredBy_activeM8BonVent =
        uiState.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent

    val relative_M10OperationVentCouleur by remember(
        selectedCouleur.keyID,
        listM10OperationVentCouleur_FilteredBy_activeM8BonVent  // FIX: was ?.size — taille inchangée
        // quand on update quantity (ex: 1→2), donc remember ne se recalculait pas et
        // renvoyait l'ancien objet avec l'ancienne quantity. La référence à la liste
        // entière invalide le remember dès que StateFlow émet une nouvelle liste (via .copy()).
    ) {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent?.find {
                it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID
            }
        }
    }

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

    val currentQuantity by remember(relative_M10OperationVentCouleur) {
        derivedStateOf {
            relative_M10OperationVentCouleur?.quantity ?: 0
        }
    }

    // LOG: currentQuantity après chaque recompose
    SideEffect {
        Log.d(
            "LenceurVent",
            "[RECOMPOSE] currentQuantity=${currentQuantity} | couleur=${selectedCouleur.keyID.takeLast(4).uppercase()}"
        )
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

    fun buildOperationToUse(newQuantity: Int): M10OperationVentCouleur =
        relative_M10OperationVentCouleur?.copy(
            quantity = newQuantity,
            parentM13TarificationKeyID = selectedTariff.keyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            parent_M1Produit_DebugInfos = "par.produit ${relative_M1produit.nom}",
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        ) ?: M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
            activeOnVent_M8BonVent,
            selectedCouleur
        ).copy(
            creationTimestamps = System.currentTimeMillis(),
            setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
            quantity = newQuantity,
            parentM13TarificationKeyID = selectedTariff.keyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            its_created_in_working_for_wholesaler = isGrossist
        )

    fun handleLenceVent(newQuantity: Int) {
        Log.d(
            "LenceurVent",
            "[LENCE] newQuantity=${newQuantity} | currentQuantity=${currentQuantity} | couleur=${selectedCouleur.keyID.takeLast(4).uppercase()}"
        )
        val operationToUse = buildOperationToUse(newQuantity)

        val isNew = listM10OperationVentCouleur_FilteredBy_activeM8BonVent
            ?.none { it.keyID == operationToUse.keyID } != false

        val quantityChange = if (isNew) operationToUse.quantity
        else operationToUse.quantity - currentQuantity

        val updatedFilteredList: List<M10OperationVentCouleur>? =
            if (isNew) (listM10OperationVentCouleur_FilteredBy_activeM8BonVent ?: emptyList()) + operationToUse
            else listM10OperationVentCouleur_FilteredBy_activeM8BonVent?.map {
                if (it.keyID == operationToUse.keyID) operationToUse else it
            }

        val updatedCouleur: M3CouleurProduitInfos? =
            if (isGrossist || quantityChange == 0) null
            else selectedCouleur.copy(count_Don_Depot = selectedCouleur.count_Don_Depot - quantityChange)

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
                set(
                    value = listM10OperationVentCouleur_FilteredBy_activeM8BonVent,
                    key = SemanticsPropertyKey("listM10OperationVentCouleur_FilteredBy_activeM8BonVent")
                )
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
            handleLenceVent(newQuantity)
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
