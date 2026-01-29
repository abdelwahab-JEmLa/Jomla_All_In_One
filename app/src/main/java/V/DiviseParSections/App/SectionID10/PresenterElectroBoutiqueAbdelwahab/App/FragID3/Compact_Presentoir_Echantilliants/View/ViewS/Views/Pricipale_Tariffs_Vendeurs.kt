package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views

import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Icon_Outlined
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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

/**
 * Displays tariff information for a product with selection capability
 *
 * @param relative_M1produit The product to display tariffs for
 * @param tariffsList List of all available tariffs
 * @param selectedTariff The currently selected tariff for this product
 * @param onTariffSelected Callback when a tariff is selected
 * @param compactMode Whether to use compact display (removes names and reduces size)
 * @param aCentralFacade Central facade for repository access
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Pricipale_Tariffs_Vendeurs_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    tariffsList: List<M13TarificationInfos>,
    selectedTariff: M13TarificationInfos,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    compactMode: Boolean = false,
    aCentralFacade: ACentralFacade = koinInject()
) {
    val displayTariffs = listOf(
        M13TarificationInfos.TypeChoisi.Prix_Detaille,
        M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
        M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
        M13TarificationInfos.TypeChoisi.Historique,
        M13TarificationInfos.TypeChoisi.LeMaxPrixArrive,
    )
    val tariffsToDisplay = displayTariffs.mapNotNull { tariffType ->
        // Find matching tariff for this product
        val matchingTariff = tariffsList.firstOrNull {
            it.typeChoisi == tariffType &&
                    it.parent_M1Produit_KeyId == relative_M1produit.keyID
        }

        // Always calculate progressive tariff
        val tariff = if (tariffType == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable) {
            matchingTariff ?: calculateProgressiveTariff(tariffsList, relative_M1produit)
        } else {
            matchingTariff
        }

        // Get price
        val prix = tariff?.prixCurrency ?: 0.0

        // For progressive tariff, always show it (even with 0.0)
        if (tariffType == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable && tariff != null) {
            tariff to prix
        } else if (prix != 0.0 && tariff != null) {
            tariff to prix
        } else {
            null
        }
    }

    // If no tariffs to display, don't render anything
    if (tariffsToDisplay.isEmpty()) {
        return
    }

    // Adjust padding based on compact mode
    val containerPadding = if (compactMode) 2.dp else 4.dp

    // Use FlowRow to wrap items when space is not available
    FlowRow(
        modifier = Modifier.padding(containerPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tariffsToDisplay.forEach { (tariff, prix) ->
            // Use key() to ensure proper recomposition and state tracking
            key(tariff.typeChoisi, relative_M1produit.keyID) {
                val isSelected = selectedTariff.typeChoisi == tariff.typeChoisi &&
                        selectedTariff.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                        tariff.parent_M1Produit_KeyId == relative_M1produit.keyID

                // Special handling for Prix_Progressive_Editable
                if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable) {
                    EditableProgressiveTariffItem(
                        tariff = tariff,
                        prix = prix,
                        nombreUnite = relative_M1produit.nombreUniteInt,
                        isSelected = isSelected,
                        compactMode = compactMode,
                        onClick = {
                            onTariffSelected(tariff)
                        },
                        onPriceUpdated = { newPrice ->
                            // Create updated tariff with new price
                            val updatedTariff = tariff.copy(
                                prixCurrency = newPrice,
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                            )

                            // Save to repository
                            aCentralFacade.repositorysMainSetter.upsert_M13TarificationInfos(updatedTariff)

                            onTariffSelected(updatedTariff)
                        }
                    )
                } else {
                    TariffItem(
                        tariff = tariff,
                        prix = prix,
                        nombreUnite = relative_M1produit.nombreUniteInt,
                        isSelected = isSelected,
                        compactMode = compactMode,
                        onClick = { onTariffSelected(tariff) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditableProgressiveTariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    onPriceUpdated: (Double) -> Unit
) {
    val horizontalPadding = if (compactMode) 6.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp
    val iconSize = if (compactMode) 4.dp else 16.dp
    val fontSize = if (compactMode) 7.sp else 12.sp
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    // Calculate unit price if nombreUnite > 1
    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix

    // Use Column in compact mode when nombreUnite > 1, otherwise Row
    if (compactMode && nombreUnite > 1) {
        Column(
            modifier = Modifier
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
                Icon_Outlined(
                    icon = icon,
                    size = iconSize,
                    tint = tariff.typeChoisi.couleur_Text,
                    modifier = Modifier.clip(CircleShape)
                )
            }

            // Editable price field
            Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
                start_count = prix,
                compact_taille = compactMode,
                textSize = fontSize,
                on_Data_Update = onPriceUpdated
            )

            // Unit price with 2 decimals for precision
            Text(
                text = String.format("(%.2f/u)", prixUnitaire),
                color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                fontSize = (fontSize.value - 1).sp
            )
        }
    } else {
        Row(
            modifier = Modifier
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
                Icon_Outlined(
                    icon = icon,
                    size = iconSize,
                    tint = tariff.typeChoisi.couleur_Text,
                    modifier = Modifier.clip(CircleShape)
                )
            }

            // Editable price field
            Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
                start_count = prix,
                compact_taille = compactMode,
                textSize = fontSize,
                on_Data_Update = onPriceUpdated
            )

            // Display the label text separately
            if (nombreUnite > 1) {
                // Unit price with 2 decimals
                Text(
                    text = String.format("DA/p.u (%.2f/u)", prixUnitaire),
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            } else {
                Text(
                    text = "DA/p.u",
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            }
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
    onClick: () -> Unit
) {
    // Adjust sizes based on compact mode
    val horizontalPadding = if (compactMode) 6.dp else 8.dp
    val verticalPadding = if (compactMode) 2.dp else 4.dp
    val iconSize = if (compactMode) 14.dp else 16.dp
    val fontSize = if (compactMode) 7.sp else 12.sp

    // Use stable border width calculation to prevent flickering
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    // Calculate unit price if nombreUnite > 1
    val prixUnitaire = if (nombreUnite > 1) prix / nombreUnite else prix

    // FIXED: Always show both prices when nombreUnite > 1
    // Use Column in compact mode for line break, Row otherwise
    if (compactMode) {
        Column(
            modifier = Modifier
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
                Icon(
                    imageVector = icon,
                    contentDescription = tariff.typeChoisi.nomArabe,
                    tint = tariff.typeChoisi.couleur_Text,
                    modifier = Modifier
                        .size(iconSize)
                        .clip(CircleShape)
                )
            }

            // Always show total price
            Text(
                text = String.format("%.0f", prix),
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize
            )

            // Always show unit price if nombreUnite > 1
            if (nombreUnite > 1) {
                Text(
                    text = String.format("(%.0f/u)", prixUnitaire),
                    color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                    fontSize = (fontSize.value - 1).sp
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
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
                    text = String.format("%.0f DA/p.u (%.0f/u)", prix, prixUnitaire),
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            } else {
                Text(
                    text = String.format("%.0f DA/p.u", prix),
                    color = tariff.typeChoisi.couleur_Text,
                    fontSize = fontSize
                )
            }
        }
    }
}

private fun calculateProgressiveTariff(
    tariffsList: List<M13TarificationInfos>,
    product: ArticlesBasesStatsTable
): M13TarificationInfos? {
    val prixDetaille = tariffsList.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                it.parent_M1Produit_KeyId == product.keyID
    }

    val prixSupperGro = tariffsList.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                it.parent_M1Produit_KeyId == product.keyID
    }

    val detaillePrice = prixDetaille?.prixCurrency ?: 0.0
    val supperGroPrice = prixSupperGro?.prixCurrency ?: 0.0

    // Calculate progressive price based on availability
    val progressivePrice = when {
        // Both available: use average
        detaillePrice > 0.0 && supperGroPrice > 0.0 -> {
            (detaillePrice + supperGroPrice) / 2.0
        }
        // Only Detaille available: use it
        detaillePrice > 0.0 -> detaillePrice
        // Only SupperGro available: use it
        supperGroPrice > 0.0 -> supperGroPrice
        // Neither available: 0.0
        else -> 0.0
    }

    // Use the first available tariff as base, or create a new one
    val baseTariff = prixDetaille ?: prixSupperGro ?: M13TarificationInfos(
        parent_M1Produit_KeyId = product.keyID,
        parent_M1Produit_DebugInfos = product.getDebugInfos()
    )

    return baseTariff.copy(
        keyID = "progressive_${product.keyID}",
        typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
        prixCurrency = progressivePrice,
        parent_M1Produit_KeyId = product.keyID
    )
}
