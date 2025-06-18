package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.round

@Composable
fun CardGauchePrixVentEtBClient(
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row {
                Text(
                    text = "📊 بيع",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(Modifier.padding(5.dp))
                Icon(
                    imageVector = Icons.Default.FireTruck,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color.Green
                )
            }
            // Client benefit calculation with proper logic
            val beneficeClient =
                (produit.clientPrixVentUnite * produit.nombreUniteInt) - produit.prixVent
            PriceEditor(
                currentPrice = beneficeClient,
                label = "ربح الزبون",
                onPriceUpdate = { newBenClient ->
                    if (produit.nombreUniteInt > 0) {
                        val newPrixVent =
                            (produit.clientPrixVentUnite * produit.nombreUniteInt) - newBenClient
                        updateProduct(produit.copy(prixVent = newPrixVent))
                    }
                },
                textColor = if (beneficeClient > 0)
                    Color(0xFFFF8C00)
                else MaterialTheme.colorScheme.error
            )

            // Unit sale price (if units > 0)
            if (produit.nombreUniteInt > 1) {
                val prixUnitVente =
                    round((produit.prixVent / produit.nombreUniteInt) * 100.0) / 100.0
                PriceEditor(
                    currentPrice = prixUnitVente,
                    label = "Unité",
                    onPriceUpdate = { newPrixUnit ->
                        val newPrixVent = newPrixUnit * produit.nombreUniteInt
                        updateProduct(produit.copy(prixVent = newPrixVent))
                    },
                    textColor = MaterialTheme.colorScheme.secondary
                )
            }
            PriceEditor(
                currentPrice = produit.prixVent,
                label = "Prix Pack",
                onPriceUpdate = { newPrix ->
                    updateProduct(produit.copy(
                        prixVent = newPrix,
                        etateActuelleOnFusionAvecBaseDonne= if(produit.prixAchat==0.0)
                            ArticlesBasesStatsTable
                                .EtateActuelleOnFusionAvecBaseDonne.PrixDeVentDefinie else
                            ArticlesBasesStatsTable
                                .EtateActuelleOnFusionAvecBaseDonne.CaprtureSonImage
                    ))
                },
                textColor = Color.Red
            )


        }
    }
}
