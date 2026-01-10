package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.View.ViewS.Views.Lenceur_Vent_Handler.View

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

@Composable
fun Lenceur_Vent_Handler_FragID4(
    relative_M1produit: ArticlesBasesStatsTable,
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    selectedCouleur: M3CouleurProduitInfos,
    selectedTariff: M13TarificationInfos,
    compactMode: Boolean = false,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    aCentralFacade: ACentralFacade = koinInject(),
    modifier: Modifier = Modifier,
) {
    var depotAlertInfo by remember { mutableStateOf<DepotUpdateResult?>(null) }
    val haptic = LocalHapticFeedback.current

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

    val isAvailable = remember(
        selectedCouleur.count_Don_Depot,
        focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    ) {
        selectedCouleur.count_Don_Depot > 0 || focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    }

    fun handleLenceVent(quantity: Int) {
        // Create or update the operation with the selected tariff
        val operationToUse = relative_M10OperationVentCouleur?.copy(
            quantity = quantity,
            parentM13TarificationKeyID = selectedTariff.keyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        ) ?: M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
            focusedValuesGetter.activeOnVent_M8BonVent,
            selectedCouleur
        ).copy(
            creationTimestamps = System.currentTimeMillis(),
            setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
            quantity = quantity,
            parentM13TarificationKeyID = selectedTariff.keyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            its_created_in_working_for_wholesaler = focusedValuesGetter.currentApp_ItsWorkChezGrossisst
        )

        lenceVent(
            relative_M10OperationVentCouleur = operationToUse,
            selectedTariff = selectedTariff,
            relative_M3CouleurInfos = selectedCouleur,
            aCentralFacade = aCentralFacade,
            onDepotUpdateFailed = { result ->
                depotAlertInfo = result
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
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
    ) {
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

fun lenceVent(
    relative_M10OperationVentCouleur: M10OperationVentCouleur,
    selectedTariff: M13TarificationInfos,
    relative_M3CouleurInfos: M3CouleurProduitInfos,
    aCentralFacade: ACentralFacade,
    onDepotUpdateFailed: (DepotUpdateResult) -> Unit
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedValuesSetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter

    val isNewOperation = relative_M10OperationVentCouleur.keyID.isEmpty() ||
            relative_M10OperationVentCouleur.keyID == "null"

    if (isNewOperation) {
        focusedValuesSetter.ajoute_New_M10OperationVentCouleur(relative_M10OperationVentCouleur)
        repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            selectedTariff,
            buildList { add(relative_M10OperationVentCouleur) },
            aCentralFacade
        )

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
        repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            selectedTariff,
            buildList { add(relative_M10OperationVentCouleur) },
            aCentralFacade
        )
        focusedValuesSetter.active_M3Couleur_pour_ouvrire_son_Dialog_choixQuantity(relative_M10OperationVentCouleur)
    }

    focusedValuesGetter.currentActive_M9AppCompt?.let { appCompt ->
        repositorysMainSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
            aCentralFacade.repositorysMainGetter.repo1ProduitInfos
                .datasValue.find { it.keyID == relative_M3CouleurInfos.parentBProduitInfosKeyID }
                ?: return@let,
            appCompt
        )
    }
}

data class DepotUpdateResult(
    val success: Boolean,
    val message: String = "",
    val currentCount: Int = 0,
    val requestedChange: Int = 0
)

fun update_countDepot(
    aCentralFacade: ACentralFacade,
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

    val newCount = couleur.count_Don_Depot + quantityChange

    if (newCount < 0) {
        return DepotUpdateResult(
            success = false,
            message = "Stock insuffisant au dépôt",
            currentCount = couleur.count_Don_Depot,
            requestedChange = quantityChange
        )
    }

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
