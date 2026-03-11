package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import kotlin.math.abs

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

    fun handleLenceVent(quantity: Int) {
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

        viewModel.lence_vent(
            operation = operationToUse,
            selectedTariff = selectedTariff,
            couleur = selectedCouleur,
            isGrossist = isGrossist,
            previousQuantity = previousQuantity,
            onDepotUpdateFailed = { result -> depotAlertInfo = result },
            onDepotUpdateSuccess = { result ->
                if (quantity < previousQuantity && !isGrossist) {
                    val returned = previousQuantity - quantity
                    Toast.makeText(
                        context,
                        "✓ Retour au dépôt: $returned unité(s)\nStock actuel: ${result.currentCount}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

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
        ) { newQuantity -> handleLenceVent(newQuantity) }
    }

    depotAlertInfo?.let { alertInfo ->
        DepotAlertDialog(
            alertInfo = alertInfo,
            onDismiss = { depotAlertInfo = null }
        )
    }
}

@Composable
private fun DepotAlertDialog(
    alertInfo: DepotUpdateResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Alerte Stock", style = MaterialTheme.typography.titleMedium) },
        text = {
            Text(
                text = buildString {
                    append(alertInfo.message)
                    append("\n\n")
                    append("Stock actuel: ${alertInfo.currentCount}")
                    append("\n")
                    append("Quantité demandée: ${abs(alertInfo.requestedChange)}")
                }
            )
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}

data class DepotUpdateResult(
    val success: Boolean,
    val message: String = "",
    val currentCount: Int = 0,
    val requestedChange: Int = 0
)
