package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

object TariffTextSizes {
    val COMPACT_MAIN_TEXT = 15.sp
    val COMPACT_SECONDARY_TEXT = 6.sp
    val COMPACT_HORIZONTAL_PADDING = 6.dp
    val COMPACT_VERTICAL_PADDING = 4.dp
    val COMPACT_ICON_SIZE = 16.dp
    val COMPACT_ICON_SIZE_TARIFF_ITEM = 14.dp

    val NORMAL_MAIN_TEXT = 18.sp
    val NORMAL_SECONDARY_TEXT = 14.sp
    val NORMAL_HORIZONTAL_PADDING = 12.dp
    val NORMAL_VERTICAL_PADDING = 8.dp
    val NORMAL_ICON_SIZE = 20.dp

    val SELECTED_BORDER_WIDTH = 3.dp
    val UNSELECTED_BORDER_WIDTH = 0.dp
}

@Composable
fun Pricipale_Tariffs_Vendeurs_FragID3(
    relative_M1produit: M01Produit,
    tariffsList: List<M13TarificationInfos>,
    selectedTariff_Par_AncienProto: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    compactMode: Boolean = false,
    modifier: Modifier = Modifier,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    une_des_selectedCouleur: M3CouleurProduitInfos,
) {
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    val listM10OperationVentCouleur_FilteredBy_activeM8BonVent =
        viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state

    val relative_M10OperationVentCouleur by remember(
        listM10OperationVentCouleur_FilteredBy_activeM8BonVent
    ) {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent?.find {
                it.parent_M3CouleurProduit_KeyID == une_des_selectedCouleur.keyID
            }
        }
    }

    val tariff_Stocked_Au_OperationVent =
        tariffsList.find {
            it.keyID == relative_M10OperationVentCouleur?.parentM13TarificationKeyID
        }

    Log.d(
        "TariffDebug",
        "[Pricipale_Tariffs] produit=${relative_M1produit.keyID} " +
                "couleur=${une_des_selectedCouleur.keyID} " +
                "m10Found=${relative_M10OperationVentCouleur?.keyID} " +
                "m10TariffKey=${relative_M10OperationVentCouleur?.parentM13TarificationKeyID} " +
                "tariff_Stocked=${tariff_Stocked_Au_OperationVent?.keyID} " +
                "tariff_StockedType=${tariff_Stocked_Au_OperationVent?.typeChoisi}"
    )

    val isGrossistMode = false
    val filteredTariffs = tariffsList.filter { tariff ->
        tariff.typeChoisi.its_gro_app == isGrossistMode && !tariff.typeChoisi.ignore_affiche
    }

    val tariffsWithEditableProgressive = if (filteredTariffs.none {
            it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable ||
                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client
        }) {
        filteredTariffs + M13TarificationInfos(
            prixCurrency = 0.0,
            parent_M1Produit_KeyId = relative_M1produit.keyID,
            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
            creationTimestamps = System.currentTimeMillis()
        )
    } else {
        filteredTariffs
    }

    val sortedTariffs = tariffsWithEditableProgressive.sortedBy { tariff ->
        when (tariff.typeChoisi) {
            M13TarificationInfos.TypeChoisi.Prix_Detaille -> 0
            M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService -> 1
            else -> tariff.typeChoisi.profitabilityScore
        }
    }

    Tariffs_MainList(
        modifier = modifier,
        sortedTariffs = sortedTariffs,
        uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
        relative_M1produit = relative_M1produit,
        selectedTariff_Par_AncienProto = selectedTariff_Par_AncienProto,
        compactMode = compactMode,
        onTariffSelected = onTariffSelected,
        tariffsList = tariffsList,
    )
}

@Composable
fun TariffItemSelector(
    tariff: M13TarificationInfos,
    relative_M1produit: M01Produit,
    isSelected: Boolean,
    compactMode: Boolean,
    onClick: () -> Unit,
    tariffsList: List<M13TarificationInfos>,
) {       //<--
    val prix = tariff.prixCurrency
    val nombreUnite = relative_M1produit.nombreUniteInt
    TariffItem(
        tariff = tariff,
        prix = prix,
        nombreUnite = nombreUnite,
        isSelected = isSelected,
        compactMode = compactMode,
        onClick = onClick,
        relative_M1produit = relative_M1produit,
        tariffsList = tariffsList
    )
}

fun formatPrice(price: Double): String =
    String.format(Locale.getDefault(), "%.0f", price)

fun formatPriceWithDecimals(price: Double): String =
    String.format(Locale.getDefault(), "%.1f", price)
