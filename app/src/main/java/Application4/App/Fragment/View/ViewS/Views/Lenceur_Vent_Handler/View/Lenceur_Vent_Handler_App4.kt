package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.CatronAdd.CartonVentHandler_App4
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Modules.Utils.M1.Module.Views.FastInit_Outlined_Int_Edite_Modulable_Proto4
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun Lenceur_Vent_Handler_App4(
    modifier: Modifier = Modifier,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    relative_M1produit: M01Produit,
    selectedCouleur: M3CouleurProduitInfos,
    selectedTariff: M13TarificationInfos,
    compactMode: Boolean = false,
    listM10OperationVentCouleur_FilteredBy_activeM8BonVent: List<M10OperationVentCouleur>?,
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel

    val activeOnVent_M8BonVent = viewModel.active_Datas.activeOnVent_M8BonVent

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

    val isGrossist = viewModel.active_Datas.currentApp_ItsWorkChezGrossisst
    val isAdmin = viewModel.active_Datas.currentApp_Est_Admin

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val au_depot by remember(
        selectedCouleur.keyID,
        viewModel.active_Datas.list_M03CouleurProduitInfos
    ) {
        derivedStateOf {
            viewModel.active_Datas.list_M03CouleurProduitInfos
                ?.find { it.keyID == selectedCouleur.keyID }
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

    // Always allow selling regardless of depot count — a zero depot is informational only.
    val isAvailable = true

    val datasValue_distinct_type = remember(uiState.list_M13TarificationInfos) {
        uiState.list_M13TarificationInfos
            .filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
            .groupBy { it.typeChoisi }
            .mapValues { (_, tariffs) -> tariffs.maxByOrNull { it.creationTimestamps } }
            .values
            .filterNotNull()
    }

    val supperGro = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                it.prixCurrency != 0.0
    }

    val detaille = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                it.prixCurrency != 0.0
    }

    val new_Prix_Progressive_Editable = M13TarificationInfos.get_default()
        .copy(typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable)

    val tariff_Stocked_Pour_NewOperationVent = supperGro
        ?: detaille
        ?: new_Prix_Progressive_Editable

    fun handleLenceVent_WhenNew(
        newQuantity: Int,
        currentList: List<M10OperationVentCouleur>?,
    ) {
        val parentM13TarificationKeyID =
            if (selectedTariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) "Prix_Progressive_Editable Non Saved"
            else selectedTariff.keyID

        val newOperation = M10OperationVentCouleur.get_Default().copy(
            creationTimestamps = System.currentTimeMillis(),
            setIN_Vent_Its_Quantity_Represent = relative_M1produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
            quantity = newQuantity,
            prix_de_Vent_entre_directement_NewProto = selectedTariff.prixCurrency,
            parentM13TarificationKeyID = parentM13TarificationKeyID,
            parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
            parent_M1Produit_KeyId = relative_M1produit.keyID,
            parent_M1Produit_DebugInfos = "par.produit ${relative_M1produit.nom}",
            parent_M3CouleurProduit_KeyID = selectedCouleur.keyID,
            parent_M3CouleurProduit_DebugInfos = selectedCouleur.get_DebugsInfos(),
            parent_M8BonVent_KeyId = activeOnVent_M8BonVent?.keyID ?: "",
            parent_M8BonVent_DebugInfos = activeOnVent_M8BonVent?.get_DebugInfos() ?: "",
            parent_M2Client_KeyID = activeOnVent_M8BonVent?.parent_M2Client_KeyID ?: "null",
            typeTarificationEnumT2 = selectedTariff.typeChoisi,
            its_created_in_working_for_wholesaler = isGrossist
        )
        val newList = (currentList ?: emptyList()) + newOperation
        viewModel.addNew_listM10OperationVentCouleur(newList)
    }

    fun handleLenceVent_When_There_Is_Old(
        newQuantity: Int,
        currentOp: M10OperationVentCouleur,
        currentList: List<M10OperationVentCouleur>?,
    ) {
        val updatedOperation = currentOp.copy(
            quantity = newQuantity,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        val updatedList = currentList?.map {
            if (it.keyID == updatedOperation.keyID) updatedOperation else it
        }
        viewModel.update_listM10OperationVentCouleur(updatedList)
    }

    // ── Dispatcher: route vers add, update, ou delete selon quantité et existence ──
    fun handleLenceVent(newQuantity: Int) {
        val currentList =
            viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
        val currentOp =
            currentList?.find { it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID }

        // When quantity drops to 0 we delete rather than update:
        // update_listM10OperationVentCouleur only maps/replaces existing entries and
        // would silently keep the filtered-out item alive in DAO + Firebase.
        when {
            newQuantity == 0 && currentOp != null -> {
                viewModel.delete_M10OperationVentCouleur(currentOp)
            }
            newQuantity == 0 -> {
                // Rien à faire : pas d'opération existante et quantité = 0
            }
            currentOp == null -> handleLenceVent_WhenNew(newQuantity, currentList)
            else -> handleLenceVent_When_There_Is_Old(newQuantity, currentOp, currentList)
        }

        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val horizontalPadding = if (compactMode) 4.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp

    val shape =
        RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)

    val containerColor = if (!isAvailable) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    } else if (currentQuantity > 0) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }
    val boitParCarton = relative_M1produit.quantite_Boit_Par_Carton
    val currentCartons = if (boitParCarton > 0) currentQuantity / boitParCarton else 0
    val depotEnCartons = if (boitParCarton > 0) au_depot / boitParCarton else 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                set(
                    value = listM10OperationVentCouleur_FilteredBy_activeM8BonVent
                        ?.filter {
                            it.parent_M1Produit_DebugInfos.contains("Lino")
                        } ?: emptyList(),
                    key = SemanticsPropertyKey("filter")
                )
                set(
                    value = tariff_Stocked_Pour_NewOperationVent,
                    key = SemanticsPropertyKey("tariff_Stocked_Pour_NewOperationVent")
                )
            },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (boitParCarton > 1 && isAdmin && viewModel.active_Datas.active_M9Compt?.affiche_ProduitDataBaseEdites_ComposableViews == true) {
            CartonVentHandler_App4(
                currentCartons = currentCartons,
                depotEnCartons = depotEnCartons,
                isAvailable = isAvailable,
                isAdmin = true,
                compactMode = compactMode,
                containerColor = containerColor,
                horizontalPadding = horizontalPadding,
                verticalPadding = verticalPadding,
                onVentUpdate = { newCartons ->
                    handleLenceVent(newCartons * boitParCarton)
                },
            )
        }

        // ── Boit / unit handler ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(containerColor.copy(alpha = 0.15f))
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            contentAlignment = Alignment.CenterEnd
        ) {
            FastInit_Outlined_Int_Edite_Modulable_Proto4(
                start_count = currentQuantity,
                au_depot = au_depot,
                standard_count = standardCount,
                icon = Icons.Default.ShoppingCart,
                isAvailable = isAvailable,
                compact_taille = compactMode,
                show_depot_card_on_top_in_flow_row = true,
                is_admin = isAdmin,
                add_spacing_between_depot_and_sale = isAdmin,
                on_admin_depot_update = { newDepotCount ->
                    viewModel.update_depot_count(selectedCouleur, newDepotCount)
                },
                on_Data_Update = { newQuantity -> handleLenceVent(newQuantity) },
            )
        }
    } // end Column
}
