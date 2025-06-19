package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.round

@Composable
fun CardDroitPrixAchatEtBenVendeur(
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
    onNextField: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    shouldHideQuickInfoCards: Boolean
) {
    val vertTurq = Color(0xFF066C62)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Section header
            Text(
                text = "🏪 شراء",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = vertTurq
            )

            val benefice = produit.prixVent - produit.prixAchat

            if (produit.prixVent > 0.0) {
                PriceEditor(
                    currentPrice = benefice,
                    label = "ربحي الخاص",
                    onPriceUpdate = { newBenefice ->
                        val newPrixVent = produit.prixAchat + newBenefice
                        updateProduct(produit.copy(prixVent = newPrixVent))
                    },
                    textColor = if (benefice > 0) {
                        vertTurq
                    } else MaterialTheme.colorScheme.error,
                )
            }

            // Unit purchase price (if units > 0)
            if (produit.nombreUniteInt > 1) {
                val prixUnitAchat =
                    round((produit.prixAchat / produit.nombreUniteInt) * 100.0) / 100.0
                PriceEditor(
                    currentPrice = prixUnitAchat,
                    label = "Unité",
                    onPriceUpdate = { newPrixUnit ->
                        val newPrixAchat = newPrixUnit * produit.nombreUniteInt
                        updateProduct(
                            produit.copy(
                                prixAchat = newPrixAchat,
                                prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
                            )
                        )
                    },
                    textColor = MaterialTheme.colorScheme.secondary,
                )
            }

            // Total purchase price
            PriceEditor(
                currentPrice = produit.prixAchat,
                label = "Prix Pack",
                onPriceUpdate = { newPrix ->
                    val newPrd = produit.copy(
                        prixAchat = newPrix,
                        prixAchatDernierTimeTempUpdate = System.currentTimeMillis(),
                        etateActuelleOnFusionAvecBaseDonne= if(produit.prixAchat==0.0)
                            ArticlesBasesStatsTable
                            .EtateActuelleOnFusionAvecBaseDonne.PrixAchatPriseDepuitGrossist else
                            ArticlesBasesStatsTable
                                .EtateActuelleOnFusionAvecBaseDonne.CaprtureSonImage
                    )
                    updateProduct(newPrd)
                },
                textColor = vertTurq,
                shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                onNextField = onNextField
            )
        }
    }
}
