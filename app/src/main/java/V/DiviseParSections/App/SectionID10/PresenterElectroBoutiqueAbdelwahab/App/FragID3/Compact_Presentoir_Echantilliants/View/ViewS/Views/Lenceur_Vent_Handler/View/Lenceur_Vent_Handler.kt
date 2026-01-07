package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views.Lenceur_Vent_Handler.View

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.OutlinedText_Avec_Init_Click_Button_Modulable_Proto3
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import kotlin.math.abs

/**
 * Composable that handles the sales operation (vent) for a specific product color.
 * Displays a button that shows current quantity and allows updates.
 *
 * Features:
 * - Shows current quantity for the selected color
 * - First click: Sets quantity to standard count (1 or full carton)
 * - Second click: Opens edit mode for custom quantity
 * - Handles stock validation (depot count)
 * - Creates new vent operations when needed
 * - Updates existing operations
 *
 * @param relative_M1produit The product information
 * @param relative_M10OperationVentCouleur Existing vent operation for this color (nullable)
 * @param selectedCouleur The color variant being sold
 * @param finale_Tariff The pricing information for this product
 * @param compactMode Whether to use compact UI sizing
 * @param attachedToImage Whether this button is attached to an image (affects styling)
 * @param focusedValuesGetter Dependency for accessing focused/active values
 * @param aCentralFacade Central facade for repository access
 * @param modifier Optional modifier
 */
@Composable
fun Lenceur_Vent_Handler_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    selectedCouleur: M3CouleurProduitInfos,
    finale_Tariff: M13TarificationInfos,
    compactMode: Boolean = false,
    attachedToImage: Boolean = true,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    aCentralFacade: ACentralFacade = koinInject(),
    modifier: Modifier = Modifier,
) {
    // State for showing depot alert dialog
    var depotAlertInfo by remember { mutableStateOf<DepotUpdateResult?>(null) }
    val haptic = LocalHapticFeedback.current

    // Get current quantity from existing operation or default to 0
    // FIXED: Added relative_M10OperationVentCouleur as remember key to trigger recomposition
    val currentQuantity by remember(relative_M10OperationVentCouleur?.keyID, relative_M10OperationVentCouleur?.quantity) {
        derivedStateOf {
            relative_M10OperationVentCouleur?.quantity ?: 0
        }
    }

    // Calculate standard count based on product settings
    // If product is set to sell by carton, use carton quantity, otherwise use 1
    val standardCount = remember(relative_M1produit.setIN_Vent_Its_Quantity_Represent) {
        if (relative_M1produit.setIN_Vent_Its_Quantity_Represent ==
            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
        )
            relative_M1produit.quantite_Boit_Par_Carton
        else
            1
    }

    // Check if item is available for sale
    // Available if: stock > 0 OR working in wholesale mode (no stock check)
    val isAvailable = remember(
        selectedCouleur.count_Don_Depot,
        focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    ) {
        selectedCouleur.count_Don_Depot > 0 || focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    }

    /**
     * Handles the sales operation when quantity is updated
     * - Updates existing operation or creates new one
     * - Updates depot count if not in wholesale mode
     * - Saves tariff and links it to operation
     */
    fun handleLenceVent(quantity: Int) {
        // Use existing operation or create default if null
        val operationToUse = relative_M10OperationVentCouleur?.copy(
            quantity = quantity,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        ) ?: M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
            focusedValuesGetter.activeOnVent_M8BonVent,
            selectedCouleur
        ).copy(
            creationTimestamps = System.currentTimeMillis(),
            setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
            quantity = quantity
        )

        // Execute the sales operation
        lenceVent(
            relative_M10OperationVentCouleur = operationToUse,
            finale_Tariff = finale_Tariff,
            relative_M3CouleurInfos = selectedCouleur,
            aCentralFacade = aCentralFacade,
            onDepotUpdateFailed = { result ->
                depotAlertInfo = result
            }
        )

        // Provide haptic feedback
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Adjust spacing based on compact mode
    val horizontalPadding = if (compactMode) 4.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp

    // Shape differs based on whether button is attached to image
    val shape = if (attachedToImage) {
        RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        )
    } else {
        RoundedCornerShape(12.dp)
    }

    // Color coding:
    // - Gray (not available): Out of stock and not in wholesale mode
    // - Tertiary (has quantity): Item already added to cart
    // - Primary (default): Available, not yet added
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
            .then(
                if (attachedToImage) {
                    Modifier
                        .clip(shape)
                        .background(containerColor.copy(alpha = 0.15f))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
    ) {
        // The reusable quantity button component
        OutlinedText_Avec_Init_Click_Button_Modulable_Proto3(
            start_count = currentQuantity,
            standard_count = standardCount,
            icon = Icons.Default.ShoppingCart,
            isAvailable = isAvailable,
            compact_taille = compactMode,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) { newQuantity ->
            handleLenceVent(newQuantity)
        }
    }

    // Show alert dialog if depot update fails (insufficient stock)
    depotAlertInfo?.let { alertInfo ->
        DepotAlertDialog(
            alertInfo = alertInfo,
            onDismiss = { depotAlertInfo = null }
        )
    }
}

/**
 * Alert dialog shown when stock/depot operation fails
 */
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

/**
 * Core function that executes the sales operation
 *
 * @param relative_M10OperationVentCouleur The operation to save (either existing updated, or new)
 * @param finale_Tariff The pricing to apply
 * @param relative_M3CouleurInfos The color being sold
 * @param aCentralFacade Central facade for repository access
 * @param onDepotUpdateFailed Callback when depot update fails
 */
fun lenceVent(
    relative_M10OperationVentCouleur: M10OperationVentCouleur,
    finale_Tariff: M13TarificationInfos,
    relative_M3CouleurInfos: M3CouleurProduitInfos,
    aCentralFacade: ACentralFacade,
    onDepotUpdateFailed: (DepotUpdateResult) -> Unit
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedValuesSetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter

    // Check if this is a new operation (no keyID from database yet)
    val isNewOperation = relative_M10OperationVentCouleur.keyID.isEmpty() ||
            relative_M10OperationVentCouleur.keyID == "null"

    if (isNewOperation) {
        // New operation - add it, save tariff, and update depot
        focusedValuesSetter.ajoute_New_M10OperationVentCouleur(relative_M10OperationVentCouleur)
        repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            finale_Tariff,
            buildList { add(relative_M10OperationVentCouleur) },
            aCentralFacade
        )

        // Update depot count if not working in wholesale mode
        if (!focusedValuesGetter.currentApp_ItsWorkChezGrossisst) {
            val result = update_countDepot(
                aCentralFacade,
                relative_M3CouleurInfos,
                -relative_M10OperationVentCouleur.quantity,
                active = focusedValuesGetter.currentApp_ItsWorkChezGrossisst
            )
            if (!result.success) {
                onDepotUpdateFailed(result)
            }
        }
    } else {
        // Existing operation - just save tariff and open dialog
        repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            finale_Tariff,
            buildList { add(relative_M10OperationVentCouleur) },
            aCentralFacade
        )
        focusedValuesSetter.active_M3Couleur_pour_ouvrire_son_Dialog_choixQuantity(relative_M10OperationVentCouleur)
    }

    // Set the focused tariff for the current product
    focusedValuesGetter.currentActive_M9AppCompt?.let { appCompt ->
        repositorysMainSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
            aCentralFacade.repositorysMainGetter.repo1ProduitInfos
                .datasValue.find { it.keyID == relative_M3CouleurInfos.parentBProduitInfosKeyID }
                ?: return@let,
            appCompt
        )
    }
}

/**
 * Data class to hold depot update result information
 */
data class DepotUpdateResult(
    val success: Boolean,
    val message: String = "",
    val currentCount: Int = 0,
    val requestedChange: Int = 0
)

/**
 * Updates the depot count for a color
 * Returns a result indicating success/failure with relevant information
 */
fun update_countDepot(
    aCentralFacade: ACentralFacade,
    couleur: M3CouleurProduitInfos,
    quantityChange: Int,
    active: Boolean
): DepotUpdateResult {
    // Skip depot update in wholesale mode
    if (active) {
        return DepotUpdateResult(
            success = true,
            message = "Depot update skipped in wholesale mode"
        )
    }

    val newCount = couleur.count_Don_Depot + quantityChange

    // Check if we have enough stock
    if (newCount < 0) {
        return DepotUpdateResult(
            success = false,
            message = "Stock insuffisant au dépôt",
            currentCount = couleur.count_Don_Depot,
            requestedChange = quantityChange
        )
    }

    // Update the color with new depot count
    val updatedCouleur = couleur.copy(
        count_Don_Depot = newCount,
        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
    )

    aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(updatedCouleur)

    return DepotUpdateResult(
        success = true,
        message = "Dépôt mis à jour avec succès",
        currentCount = newCount,
        requestedChange = quantityChange
    )
}
