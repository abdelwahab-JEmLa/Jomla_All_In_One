package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
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
import androidx.compose.runtime.collectAsState
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
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, ViewModel_NewProtoPatterns>,
    relative_M1produit: M01Produit,
    relative_M8BonVent: M8BonVent? = null,
    selectedCouleur: M3CouleurProduitInfos,
    selectedTariff: M13TarificationInfos,
    compactMode: Boolean = false,
    modifier: Modifier = Modifier,
    isWifiClientConnected: Boolean = false,
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val centralValues = uiState.active_Central_Values

    val activeOnVent_M8BonVent by viewModel.activeOnVent_M8BonVent_flow.collectAsState()

    val listM10OperationVentCouleur_FilteredBy_activeM8BonVent =
        viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state

    val relative_M10OperationVentCouleur by remember(
        selectedCouleur.keyID,
        listM10OperationVentCouleur_FilteredBy_activeM8BonVent
    ) {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent?.find {
                it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID
            }
        }
    }

    val isGrossist = centralValues.activeCompt?.travailleChezGrossisst3Ali == true
    val isAdmin = centralValues.currentApp_Est_Admin

    var depotAlertInfo by remember { mutableStateOf<DepotUpdateResult?>(null) }

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val au_depot by remember(selectedCouleur.keyID, uiState.list_M3CouleurProduit) {
        derivedStateOf {
            uiState.list_M3CouleurProduit
                .find { it.keyID == selectedCouleur.keyID }
                ?.count_Don_Depot ?: selectedCouleur.count_Don_Depot
        }
    }

    val currentQuantity by remember(relative_M10OperationVentCouleur) {
        derivedStateOf { relative_M10OperationVentCouleur?.quantity ?: 0 }
    }

    val standardCount = remember(relative_M1produit.setIN_Vent_Its_Quantity_Represent) {
        if (relative_M1produit.setIN_Vent_Its_Quantity_Represent ==
            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
        ) relative_M1produit.quantite_Boit_Par_Carton
        else 1
    }

    val isAvailable by remember(au_depot, isGrossist) {
        derivedStateOf { au_depot > 0 || isGrossist }
    }

    fun handleDepotUpdate(newDepotCount: Int) {
        viewModel.update_depot_count(
            couleur = selectedCouleur,
            newDepotCount = newDepotCount,
            onSuccess = {
                Toast.makeText(context, "✓ Dépôt mis à jour: $newDepotCount unité(s)", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun handleLenceVent(newQuantity: Int) {

        val currentList = viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
        val currentOp   = currentList?.find { it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID }

        val operationToUse = currentOp?.copy(
            quantity = newQuantity,
            parentM13TarificationKeyID = selectedTariff.keyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            parent_M1Produit_DebugInfos = "par.produit ${relative_M1produit.nom}",
            parent_M8BonVent_KeyId = activeOnVent_M8BonVent?.keyID ?: "",
            parent_M8BonVent_DebugInfos = activeOnVent_M8BonVent?.get_DebugInfos() ?: "",
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        ) ?: M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
            activeOnVent_M8BonVent, selectedCouleur
        ).copy(
            creationTimestamps = System.currentTimeMillis(),
            setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
            quantity = newQuantity,
            parentM13TarificationKeyID = selectedTariff.keyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            parent_M8BonVent_KeyId = activeOnVent_M8BonVent?.keyID ?: "",
            parent_M8BonVent_DebugInfos = activeOnVent_M8BonVent?.get_DebugInfos() ?: "",
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            its_created_in_working_for_wholesaler = isGrossist
        )

        val isNew = currentList?.none { it.keyID == operationToUse.keyID } != false
        val quantityChange = if (isNew) operationToUse.quantity else operationToUse.quantity - (currentOp?.quantity ?: 0)

        val updatedFilteredList =
            if (isNew) (currentList ?: emptyList()) + operationToUse
            else currentList?.map { if (it.keyID == operationToUse.keyID) operationToUse else it }

        val sizeBefore = viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state?.size
        viewModel.update_listM10OperationVentCouleur_FilteredBy_activeM8BonVent(updatedFilteredList)
        val sizeAfter  = viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state?.size
        val opAfter    = viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
            ?.find { it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID }

        val updatedCouleur =
            if (isGrossist || quantityChange == 0) null
            else selectedCouleur.copy(count_Don_Depot = selectedCouleur.count_Don_Depot - quantityChange)

        if (updatedCouleur != null) {

            viewModel.update_m3couleur(updatedCouleur)
        } else {

        }

        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val horizontalPadding = if (compactMode) 4.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp

    val shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)

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
            icon = Icons.Default.ShoppingCart,
            isAvailable = isAvailable,
            compact_taille = compactMode,
            show_depot_card_on_top_in_flow_row = true,
            is_admin = isAdmin,
            add_spacing_between_depot_and_sale = isAdmin,
            on_admin_depot_update = { newDepotCount -> handleDepotUpdate(newDepotCount) },
            on_Data_Update = { newQuantity -> handleLenceVent(newQuantity) },
        )
    }

    depotAlertInfo?.let { alertInfo ->
        DepotAlertDialog(alertInfo = alertInfo, onDismiss = { depotAlertInfo = null })
    }
}

data class DepotUpdateResult(
    val success: Boolean,
    val message: String = "",
    val currentCount: Int = 0,
    val requestedChange: Int = 0
)
