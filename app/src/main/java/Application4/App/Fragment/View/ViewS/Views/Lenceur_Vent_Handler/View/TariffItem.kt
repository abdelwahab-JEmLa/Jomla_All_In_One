package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M13TarificationInfos.TypeChoisi
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TariffItem(
    tariff: M13TarificationInfos,
    prix: Double,
    nombreUnite: Int,
    isSelected: Boolean,
    compactMode: Boolean = false,
    onClick: () -> Unit,
    relative_M1produit: M01Produit,
    tariffsList: List<M13TarificationInfos>,
    modifier: Modifier = Modifier.Companion
) {
    val effectivePrix =
        if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) {
            val detailleTariff = tariffsList.find {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                        it.parent_M1Produit_KeyId == relative_M1produit.keyID && it.prixCurrency != 0.0
            }
            val supperGroTariff = tariffsList.find {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                        it.parent_M1Produit_KeyId == relative_M1produit.keyID && it.prixCurrency != 0.0
            }
            M13TarificationInfos.Companion.remembered_calculated_progressive_changement_tariff(
                relative_Prix_Detaille = detailleTariff?.prixCurrency,
                relative_Prix_SupperGro_Et_PresentationService = supperGroTariff?.prixCurrency,
                relative_produit = relative_M1produit
            )?.prixCurrency ?: prix
        } else {
            prix
        }

    if (effectivePrix == 0.0) return

    val horizontalPadding =
        if (compactMode) TariffTextSizes.COMPACT_HORIZONTAL_PADDING else TariffTextSizes.NORMAL_HORIZONTAL_PADDING
    val verticalPadding =
        if (compactMode) TariffTextSizes.COMPACT_VERTICAL_PADDING else TariffTextSizes.NORMAL_VERTICAL_PADDING
    val fontSize =
        if (compactMode) TariffTextSizes.COMPACT_MAIN_TEXT else TariffTextSizes.NORMAL_MAIN_TEXT
    val secondaryFontSize =
        if (compactMode) TariffTextSizes.COMPACT_SECONDARY_TEXT else TariffTextSizes.NORMAL_SECONDARY_TEXT
    val borderWidth =
        if (isSelected) TariffTextSizes.SELECTED_BORDER_WIDTH else TariffTextSizes.UNSELECTED_BORDER_WIDTH
    val borderColor = if (isSelected) Color.Companion.Red else Color.Companion.Transparent
    val backgroundColor =
        if (tariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService) {
            Color.Companion.Black.copy(alpha = if (isSelected) 1f else 0.9f)
        } else {
            tariff.typeChoisi.couleur.copy(alpha = if (isSelected) 1f else 0.9f)
        }
    val prixUnitaire = if (nombreUnite > 1) effectivePrix / nombreUnite else effectivePrix
    val clientPrixVentUnite = relative_M1produit.clientPrixVentUnite
    val beneficeClient = clientPrixVentUnite * nombreUnite - effectivePrix

    if (compactMode) {
        Column(
            modifier = modifier
                .border(width = borderWidth, color = borderColor, shape = CircleShape)
                .background(color = backgroundColor, shape = CircleShape)
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
        ) {
            Text(
                text = formatPrice(effectivePrix),
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize,
                lineHeight = fontSize,
                modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
            )


            if (nombreUnite > 1) {
                Text(
                    text = "(${formatPriceWithDecimals(prixUnitaire)}/u)",
                    color = tariff.typeChoisi.couleur_Text.copy(alpha = 0.8f),
                    fontSize = secondaryFontSize,
                    lineHeight = secondaryFontSize,
                    modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
                )
            }

            if (clientPrixVentUnite > 0) {
                Text(
                    text = "b.cli: ${formatPrice(beneficeClient)} DA",
                    color = if (beneficeClient >= 0) tariff.typeChoisi.couleur_Text.copy(alpha = 0.75f) else Color.Blue.copy(
                        alpha = 0.85f
                    ),
                    fontSize = secondaryFontSize,
                    lineHeight = secondaryFontSize,
                    modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
                )
            }

            Text(
                text = "m.b: ${formatPrice(tariff.prixCurrency - tariffsList.last { it.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros }.prixCurrency)} DA",
                color = if (beneficeClient >= 0) tariff.typeChoisi.couleur_Text.copy(alpha = 0.75f) else Color.Red.copy(
                    alpha = 0.85f
                ),
                fontSize = secondaryFontSize,
                lineHeight = secondaryFontSize,
                modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
            )
        }
    } else {
        Column(
            modifier = modifier
                .border(width = borderWidth, color = borderColor, shape = CircleShape)
                .background(color = backgroundColor, shape = CircleShape)
                .clickable(onClick = onClick)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
        ) {
            Text(
                text = tariff.typeChoisi.abrgNom,
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize,
                modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
            )
            Text(
                text = if (nombreUnite > 1)
                    "${formatPrice(effectivePrix)} DA/p.u (${formatPriceWithDecimals(prixUnitaire)}/u)"
                else
                    "${formatPrice(effectivePrix)} DA/p.u",
                color = tariff.typeChoisi.couleur_Text,
                fontSize = fontSize,
                modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
            )
            if (clientPrixVentUnite > 0) {
                Text(
                    text = "bén: ${formatPrice(beneficeClient)} DA",
                    color = if (beneficeClient >= 0) tariff.typeChoisi.couleur_Text.copy(alpha = 0.75f) else Color.Companion.Red.copy(
                        alpha = 0.85f
                    ),
                    fontSize = secondaryFontSize,
                    lineHeight = secondaryFontSize,
                    modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
                )
            }
        }
    }
}
