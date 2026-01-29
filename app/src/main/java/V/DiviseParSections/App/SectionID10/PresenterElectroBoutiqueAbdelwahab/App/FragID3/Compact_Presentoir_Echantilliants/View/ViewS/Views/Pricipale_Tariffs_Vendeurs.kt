package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import java.util.Locale

private object TariffTextSizes {
    // Compact mode sizes
    val COMPACT_MAIN_TEXT = 15.sp
    val COMPACT_SECONDARY_TEXT = 6.sp
    val COMPACT_HORIZONTAL_PADDING = 8.dp
    val COMPACT_VERTICAL_PADDING = 4.dp
    val COMPACT_ICON_SIZE = 16.dp
    val COMPACT_ICON_SIZE_TARIFF_ITEM = 14.dp

    // Normal mode sizes
    val NORMAL_MAIN_TEXT = 18.sp
    val NORMAL_SECONDARY_TEXT = 14.sp
    val NORMAL_HORIZONTAL_PADDING = 12.dp
    val NORMAL_VERTICAL_PADDING = 8.dp
    val NORMAL_ICON_SIZE = 20.dp
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Pricipale_Tariffs_Vendeurs_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    tariffsList: List<M13TarificationInfos>,
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    compactMode: Boolean = false,
    aCentralFacade: ACentralFacade = koinInject(),
    modifier: Modifier = Modifier
) {
    val isGrossistMode = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    val filteredTariffs = tariffsList.filter { tariff ->
        tariff.typeChoisi.its_gro_app == isGrossistMode
    }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        filteredTariffs.forEach { tariff ->
            key(tariff.keyID) {
                TariffItemSelector(
                    tariff = tariff,
                    relative_M1produit = relative_M1produit,
                    isSelected = tariff.keyID == selectedTariff.keyID,
                    compactMode = compactMode,
                    onClick = { onTariffSelected(tariff) },
                    aCentralFacade = aCentralFacade
                )
            }
        }
    }
}

@Composable
private fun TariffItemSelector(
    tariff: M13TarificationInfos,
    relative_M1produit: ArticlesBasesStatsTable,
    isSelected: Boolean,
    compactMode: Boolean,
    onClick: () -> Unit,
    aCentralFacade: ACentralFacade
) {
    val prix = tariff.prixCurrency
    val nombreUnite = relative_M1produit.quantite_Boit_Par_Carton

    when {
        tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable ||
                tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive -> {
            EditableProgressiveTariffItem(
                tariff = tariff,
                prix = prix,
                nombreUnite = nombreUnite,
                isSelected = isSelected,
                compactMode = compactMode,
                onClick = onClick,
                onPriceUpdated = { newPrice ->
                    handleProgressivePriceUpdate(
                        tariff = tariff,
                        newPrice = newPrice,
                        aCentralFacade = aCentralFacade
                    )
                }
            )
        }

        else -> {
            TariffItem(
                tariff = tariff,
                prix = prix,
                nombreUnite = nombreUnite,
                isSelected = isSelected,
                compactMode = compactMode,
                onClick = onClick
            )
        }
    }
}

private fun handleProgressivePriceUpdate(
    tariff: M13TarificationInfos,
    newPrice: Double,
    aCentralFacade: ACentralFacade
) {
    Log.d(
        "PricipaleTariffsVendeurs",
        "handleProgressivePriceUpdate - Tariff: ${tariff.keyID}, New Price: $newPrice"
    )

    if (tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable &&
        tariff.typeChoisi != M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Progressive) {
        Log.w(
            "PricipaleTariffsVendeurs",
            "Attempted to edit non-progressive tariff: ${tariff.typeChoisi}"
        )
        return
    }

    if (newPrice <= 0) {
        Log.w(
            "PricipaleTariffsVendeurs",
            "Invalid price value: $newPrice. Must be greater than 0."
        )
        return
    }

    val updatedTariff = tariff.copy(
        prixCurrency = newPrice,
        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
    )

    Log.d(
        "PricipaleTariffsVendeurs",
        "Saving updated tariff: ${updatedTariff.keyID} with price: ${updatedTariff.prixCurrency}"
    )

    aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)
}

private fun formatPrice(price: Double): String {
    return String.format(Locale.getDefault(), "%.0f", price)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditableProgressiveTariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    onPriceUpdated: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    // Using constants for sizes
    val horizontalPadding = if (compactMode) {
        TariffTextSizes.COMPACT_HORIZONTAL_PADDING
    } else {
        TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    }
    val verticalPadding = if (compactMode) {
        TariffTextSizes.COMPACT_VERTICAL_PADDING
    } else {
        TariffTextSizes.NORMAL_VERTICAL_PADDING
    }
    val iconSize = if (compactMode) {
        TariffTextSizes.COMPACT_ICON_SIZE
    } else {
        TariffTextSizes.NORMAL_ICON_SIZE
    }
    val fontSize = if (compactMode) {
        TariffTextSizes.COMPACT_MAIN_TEXT
    } else {
        TariffTextSizes.NORMAL_MAIN_TEXT
    }

    // Use stable border width calculation to prevent flickering
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    // Calculate unit price if nombreUnite > 1
    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix

    FlowRow(
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = CircleShape
            )
            .background(
                color = tariff.typeChoisi.couleur.copy(
                    alpha = if (isSelected) 1f else 0.9f
                ),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        maxItemsInEachRow = Int.MAX_VALUE // Allow unlimited items per row
    ) {
        Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
            value = prix,
            onValueChanged = { newValue ->
                onPriceUpdated(newValue)
            },
            compact_taille = compactMode,
            textSize = fontSize,
            containerColor = tariff.typeChoisi.couleur,
            textColor = tariff.typeChoisi.couleur_Text,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        // Show unit price if nombreUnite > 1
        if (nombreUnite > 1) {
            Text(
                text = "(${formatPrice(prixUnitaire)}/u)",
                color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                fontSize = fontSize,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun TariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Using constants for sizes
    val horizontalPadding = if (compactMode) {
        TariffTextSizes.COMPACT_HORIZONTAL_PADDING
    } else {
        TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    }
    val verticalPadding = if (compactMode) {
        TariffTextSizes.COMPACT_VERTICAL_PADDING
    } else {
        TariffTextSizes.NORMAL_VERTICAL_PADDING
    }
    val iconSize = if (compactMode) {
        TariffTextSizes.COMPACT_ICON_SIZE_TARIFF_ITEM
    } else {
        TariffTextSizes.NORMAL_ICON_SIZE
    }
    val fontSize = if (compactMode) {
        TariffTextSizes.COMPACT_MAIN_TEXT
    } else {
        TariffTextSizes.NORMAL_MAIN_TEXT
    }
    val secondaryFontSize = if (compactMode) {
        TariffTextSizes.COMPACT_SECONDARY_TEXT
    } else {
        TariffTextSizes.NORMAL_SECONDARY_TEXT
    }

    // Use stable border width calculation to prevent flickering
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    // Calculate unit price if nombreUnite > 1
    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix

    // FIXED: Always show both prices when nombreUnite > 1
    // Use Column in compact mode for line break, Row otherwise
    if (compactMode) {
        Column(
            modifier = modifier
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    color = tariff.typeChoisi.couleur.copy(
                        alpha = if (isSelected) 1f else 0.9f
                    ),
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            tariff.typeChoisi.iconVector?.let { icon ->

            }

            // Always show total price
            Text(
                text = formatPrice(prix),
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )

            // Always show unit price if nombreUnite > 1
            if (nombreUnite > 1) {
                Text(
                    text = "(${formatPrice(prixUnitaire)}/u)",
                    color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                    fontSize = secondaryFontSize
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    color = tariff.typeChoisi.couleur.copy(
                        alpha = if (isSelected) 1f else 0.9f
                    ),
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            tariff.typeChoisi.iconVector?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = tariff.typeChoisi.nomArabe,
                    tint = tariff.typeChoisi.couleur_Text,
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                )
            }

            // Abbreviated name
            Text(
                text = tariff.typeChoisi.abrgNom,
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )

            // Always show both prices if nombreUnite > 1
            if (nombreUnite > 1) {
                Text(
                    text = "${formatPrice(prix)} DA/p.u (${formatPrice(prixUnitaire)}/u)",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            } else {
                Text(
                    text = "${formatPrice(prix)} DA/p.u",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            }
        }
    }
}
