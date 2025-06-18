package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.round

@Composable
fun DetailleSection(
    modifier: Modifier,
    shouldHideQuickInfoCards: Boolean,
    showDetailsExpanded: Boolean,
    onNextField: (() -> Unit)? = null,
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    if (showDetailsExpanded) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Improved cards layout with better spacing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!shouldHideQuickInfoCards) {
                        // Left Card - Client Sales
                        CardGauchePrixVentEtBClient(
                            produit = produit,
                            updateProduct = updateProduct,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Right Card - Purchase & Profit
                    CardDroitPrixAchatEtBenVendeur(
                        shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                        produit = produit,
                        updateProduct = updateProduct,
                        onNextField = onNextField,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Unit section with better styling
                PriceAndUnitSection(
                    produit = produit,
                    updateProduct = updateProduct
                )
            }
        }
    }
}

@Composable
private fun CardGauchePrixVentEtBClient(
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
            Text(
                text = "📊 Vente",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            PriceEditor(
                currentPrice = produit.prixVent,
                label = "Total",
                onPriceUpdate = { newPrix ->
                    updateProduct(produit.copy(prixVent = newPrix))
                },
                textColor = MaterialTheme.colorScheme.primary
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

            // Client benefit calculation with proper logic
            val beneficeClient = produit.clientPrixVentUnite * produit.nombreUniteInt
            PriceEditor(
                currentPrice = beneficeClient,
                label = "Bénéfice",
                onPriceUpdate = { newBenefice ->
                    // Calculate new client unit price based on desired total benefit
                    if (produit.nombreUniteInt > 0) {
                        val newClientPrixUnite = newBenefice / produit.nombreUniteInt
                        updateProduct(produit.copy(clientPrixVentUnite = newClientPrixUnite))
                    }
                },
                textColor = if (beneficeClient > 0) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}

// Fixed B3DetailleSection.kt - Remove local focus requesters and use passed onNextField
@Composable
private fun CardDroitPrixAchatEtBenVendeur(
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
    onNextField: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    shouldHideQuickInfoCards: Boolean
) {
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
                text = "🏪 Achat",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )

            // Total purchase price
            PriceEditor(
                currentPrice = produit.prixAchat,
                label = "Total",
                onPriceUpdate = { newPrix ->
                    val newPrd = produit.copy(
                        prixAchat = newPrix,
                        prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
                    )
                    updateProduct(newPrd)
                },
                showOnlyWhenPositive = true,
                textColor = MaterialTheme.colorScheme.tertiary,
                shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                onNextField = onNextField
            )

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
                    shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                    onNextField = onNextField
                )
            }

            if (produit.clientPrixVentUnite > 0.0) {
                // Seller profit calculation
                val benefice = produit.prixVent - produit.prixAchat
                PriceEditor(
                    currentPrice = benefice,
                    label = "Profit",
                    onPriceUpdate = { newBenefice ->
                        val newPrixVent = produit.prixAchat + newBenefice
                        updateProduct(produit.copy(prixVent = newPrixVent))
                    },
                    textColor = if (benefice > 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                    onNextField = onNextField
                )
            }
        }
    }
}

