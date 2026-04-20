package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.ViewS.Views.Lenceur_Vent_Handler.View

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.OutlinedText_Avec_Init_Click_Button_Modulable_Proto2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import kotlin.math.abs

/**
 * Composable handler for launching sale operations (lenceVent)
 * Includes quantity button and depot alert handling
 *
 * @param relative_M1produit The product being sold
 * @param selectedCouleur The currently selected color variant
 * @param finale_Tariff The tariff/pricing to use for this sale
 * @param focusedValuesGetter Getter for focused values (injected by default)
 * @param aCentralFacade Central facade for repository access (injected by default)
 * @param modifier Optional modifier
 */

@Composable
fun Lenceur_Vent_Handler(
    relative_M1produit: M01Produit,
    selectedCouleur: M3CouleurProduitInfos,
    finale_Tariff: M13TarificationInfos,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    aCentralFacade: ACentralFacade = koinInject(),
    modifier: Modifier = Modifier
) {
    var depotAlertInfo by remember { mutableStateOf<DepotUpdateResult?>(null) }
    val haptic = LocalHapticFeedback.current

    // Get current quantity for selected color
    val currentQuantity by remember {
        derivedStateOf {
            focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                .find { it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID }
                ?.quantity ?: 0
        }
    }

    // Determine standard count based on product quantity representation
    val standardCount = remember(relative_M1produit.setIN_Vent_Its_Quantity_Represent) {
        if (relative_M1produit.setIN_Vent_Its_Quantity_Represent ==
            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton)
            relative_M1produit.quantite_Boit_Par_Carton
        else
            1
    }

    // Check if item is available (in stock or working in wholesale mode)
    val isAvailable = remember(selectedCouleur.count_Don_Depot, focusedValuesGetter.currentApp_ItsWorkChezGrossisst) {
        selectedCouleur.count_Don_Depot > 0 || focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    }

    fun handleLenceVent(quantity: Int) {
        // Find existing vent operation for this color
        val relative_M10OperationVentCouleur = focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .find { it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID }

        // Create default vent if needed
        val defaultM10Vent = M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
            focusedValuesGetter.activeOnVent_M8BonVent,
            selectedCouleur
        ).copy(
            creationTimestamps = System.currentTimeMillis(),
            setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
            quantity = quantity
        )

        // Execute the sale operation
        lenceVent(
            relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
            defaultM10Vent = defaultM10Vent,
            finale_Tariff = finale_Tariff,
            relative_M3CouleurInfos = selectedCouleur,
            aCentralFacade = aCentralFacade,
            onDepotUpdateFailed = { result ->
                depotAlertInfo = result
            }
        )

        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        OutlinedText_Avec_Init_Click_Button_Modulable_Proto2(
            start_count = currentQuantity,
            standard_count = standardCount,
            icon = Icons.Default.ShoppingCart,
            enabled = isAvailable,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) { newQuantity ->
            handleLenceVent(newQuantity)
        }
    }

    // Show depot alert dialog if stock is insufficient
    depotAlertInfo?.let { alertInfo ->
        DepotAlertDialog(
            alertInfo = alertInfo,
            onDismiss = { depotAlertInfo = null }
        )
    }
}

/**
 * Alert dialog for depot/stock issues
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
