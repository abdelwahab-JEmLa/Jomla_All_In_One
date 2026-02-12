package com.example.clientjetpack.App2.App.View.ViewS.Views.Lenceur_Vent_Handler.View

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.FastInit_Outlined_Int_Edite_Modulable_Proto3
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
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
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainSetter_app2
import org.koin.compose.koinInject
import kotlin.math.abs

@Composable
fun Lenceur_Vent_Handler_AppEcranPresntoireJemlaCom(
    relative_M1produit: ArticlesBasesStatsTable,
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    selectedCouleur: M3CouleurProduitInfos,
    selectedTariff: M13TarificationInfos,
    compactMode: Boolean = false,
    focusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
    repositorysMainGetter_app2: RepositorysMainGetter_app2  = koinInject(),
    repositorysMainSetter_app2: RepositorysMainSetter_app2 = koinInject(),
    modifier: Modifier = Modifier,
    isWifiClientConnected: Boolean = false,
) {
    // FIXED: Added spacing parameter to prevent accidental clicks between depot and sale button
    var depotAlertInfo by remember { mutableStateOf<DepotUpdateResult?>(null) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    // FIXED: Observe depot count from repository for automatic recomposition
    val au_depot by remember(selectedCouleur.keyID) {
        derivedStateOf {
            repositorysMainGetter_app2.repo03CouleurProduitInfos.datasValue
                .find { it.keyID == selectedCouleur.keyID }
                ?.count_Don_Depot ?: selectedCouleur.count_Don_Depot
        }
    }

    val currentQuantity by remember(relative_M10OperationVentCouleur?.keyID, relative_M10OperationVentCouleur?.quantity) {
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

    // FIXED: Use the observed au_depot value for availability check
    val isAvailable by remember(au_depot, focusedValuesGetter_app2.currentApp_ItsWorkChezGrossisst) {
        derivedStateOf {
            au_depot > 0 || focusedValuesGetter_app2.currentApp_ItsWorkChezGrossisst
        }
    }

    // Check if user is admin
    val isAdmin = remember { focusedValuesGetter_app2.currentApp_Est_Admin }

    fun handleDepotUpdate(newDepotCount: Int) {
        val updatedCouleur = selectedCouleur.copy(
            count_Don_Depot = newDepotCount,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        repositorysMainGetter_app2.repo03CouleurProduitInfos.addOrUpdateData(updatedCouleur)

        // Show success toast
        Toast.makeText(
            context,
            "✓ Dépôt mis à jour: $newDepotCount unité(s)",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun handleLenceVent(quantity: Int) {
        val previousQuantity = currentQuantity

        // Create or update the operation with the selected tariff
        val operationToUse = relative_M10OperationVentCouleur?.copy(
            quantity = quantity,
            parentM13TarificationKeyID = selectedTariff.keyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        ) ?: M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
            focusedValuesGetter_app2.activeOnVent_M8BonVent,
            selectedCouleur
        ).copy(
            creationTimestamps = System.currentTimeMillis(),
            setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
            quantity = quantity,
            parentM13TarificationKeyID = selectedTariff.keyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            its_created_in_working_for_wholesaler = focusedValuesGetter_app2.currentApp_ItsWorkChezGrossisst
        )

        lenceVent(
            repositorysMainGetter_app2=repositorysMainGetter_app2,
            focusedValuesGetter_app2=focusedValuesGetter_app2,
            repositorysMainSetter_app2=repositorysMainSetter_app2,
            relative_M10OperationVentCouleur = operationToUse,
            selectedTariff = selectedTariff,
            relative_M3CouleurInfos = selectedCouleur,
            onDepotUpdateFailed = { result ->
                depotAlertInfo = result
            },
            onDepotUpdateSuccess = { result ->
                // FIXED: Show long Toast when items are returned to depot (quantity decreased)
                if (quantity < previousQuantity && !focusedValuesGetter_app2.currentApp_ItsWorkChezGrossisst) {
                    val returnedQuantity = previousQuantity - quantity
                    Toast.makeText(
                        context,
                        "✓ Retour au dépôt: $returnedQuantity unité(s)\nStock actuel: ${result.currentCount}",
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
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 12.dp,
        bottomEnd = 12.dp
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
            // FIXED: Add spacing between depot and sale button for admin to prevent accidental clicks
            add_spacing_between_depot_and_sale = isAdmin,
            on_admin_depot_update = { newDepotCount ->
                handleDepotUpdate(newDepotCount)
            }
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

@Composable
private fun DepotAlertDialog(
    alertInfo: DepotUpdateResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Alerte Stock",
                style = MaterialTheme.typography.titleMedium
            )
        },
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
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

// FIXED: Added onDepotUpdateSuccess callback parameter
fun lenceVent(
    relative_M10OperationVentCouleur: M10OperationVentCouleur,
    selectedTariff: M13TarificationInfos,
    relative_M3CouleurInfos: M3CouleurProduitInfos,
    onDepotUpdateFailed: (DepotUpdateResult) -> Unit,
    onDepotUpdateSuccess: (DepotUpdateResult) -> Unit = {},
    repositorysMainGetter_app2: RepositorysMainGetter_app2,
    focusedValuesGetter_app2: FocusedValuesGetter_app2,
    repositorysMainSetter_app2: RepositorysMainSetter_app2
) {
    val focusedValuesGetter_app2 = focusedValuesGetter_app2

    val isNewOperation = relative_M10OperationVentCouleur.keyID.isEmpty() ||
            relative_M10OperationVentCouleur.keyID == "null"

    if (isNewOperation) {
        repositorysMainGetter_app2.repo10OperationVentCouleur.add_New(relative_M10OperationVentCouleur)
        repositorysMainSetter_app2.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            selectedTariff,
            buildList { add(relative_M10OperationVentCouleur) },
        )

        if (!focusedValuesGetter_app2.currentApp_ItsWorkChezGrossisst) {
            val result = update_countDepot(
                repositorysMainGetter_app2=repositorysMainGetter_app2,
                relative_M3CouleurInfos,
                relative_M10OperationVentCouleur.quantity,
                active = focusedValuesGetter_app2.currentApp_ItsWorkChezGrossisst
            )
            if (!result.success) {
                onDepotUpdateFailed(result)
            } else {
                onDepotUpdateSuccess(result)
            }
        }
    } else {
        // Get the previous quantity to calculate the difference
        val existingOperation = focusedValuesGetter_app2.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .find { it.keyID == relative_M10OperationVentCouleur.keyID }
        val previousQuantity = existingOperation?.quantity ?: 0
        val quantityDifference = relative_M10OperationVentCouleur.quantity - previousQuantity

        // Update the operation
        repositorysMainSetter_app2.add_New_M10OperationVentCouleur(relative_M10OperationVentCouleur)

        repositorysMainSetter_app2.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            selectedTariff,
            buildList { add(relative_M10OperationVentCouleur) },
        )

        // Handle depot update for existing operations - only update the difference
        if (!focusedValuesGetter_app2.currentApp_ItsWorkChezGrossisst && quantityDifference != 0) {
            val result = update_countDepot(
                repositorysMainGetter_app2=repositorysMainGetter_app2,
                relative_M3CouleurInfos,
                -quantityDifference,
                active = focusedValuesGetter_app2.currentApp_ItsWorkChezGrossisst
            )
            if (!result.success) {
                onDepotUpdateFailed(result)
            } else {
                onDepotUpdateSuccess(result)
            }
        }
    }
}

data class DepotUpdateResult(
    val success: Boolean,
    val message: String = "",
    val currentCount: Int = 0,
    val requestedChange: Int = 0
)

fun update_countDepot(
    repositorysMainGetter_app2: RepositorysMainGetter_app2,
    couleur: M3CouleurProduitInfos,
    quantityChange: Int,
    active: Boolean
): DepotUpdateResult {
    if (active) {
        return DepotUpdateResult(
            success = true,
            message = "Depot update skipped in wholesale mode"
        )
    }

    // FIXED: Get current depot count from repository to avoid stale data
    val currentCouleur = repositorysMainGetter_app2.repo03CouleurProduitInfos.datasValue
        .find { it.keyID == couleur.keyID } ?: couleur

    val currentDepotCount = currentCouleur.count_Don_Depot
    val newCount = currentDepotCount + quantityChange

    if (newCount < 0) {
        return DepotUpdateResult(
            success = false,
            message = "Stock insuffisant au dépôt",
            currentCount = currentDepotCount,
            requestedChange = quantityChange
        )
    }

    val updatedCouleur = currentCouleur.copy(
        count_Don_Depot = newCount,
        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
    )

    repositorysMainGetter_app2.repo03CouleurProduitInfos.addOrUpdateData(updatedCouleur)

    return DepotUpdateResult(
        success = true,
        message = "Dépôt mis à jour avec succès",
        currentCount = newCount,
        requestedChange = quantityChange
    )
}
