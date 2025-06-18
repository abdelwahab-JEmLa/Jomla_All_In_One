package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
    showDetailsExpanded: Boolean,
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    if (showDetailsExpanded) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Section Header with improved styling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "💰 Prix et Calculs",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Improved cards layout with better spacing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Left Card - Client Sales
                    CardGauchePrixVentEtBClient(
                        produit = produit,
                        updateProduct = updateProduct,
                        modifier = Modifier.weight(1f)
                    )

                    // Right Card - Purchase & Profit
                    CardDroitPrixAchatEtBenVendeur(
                        produit = produit,
                        updateProduct = updateProduct,
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
    modifier: Modifier = Modifier
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
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Section header
            Text(
                text = "📊 Vente",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Total sale price
            PriceEditor(
                currentPrice = produit.prixVent,
                label = "Total",
                onPriceUpdate = { newPrix ->
                    updateProduct(produit.copy(prixVent = newPrix))
                },
                textColor = MaterialTheme.colorScheme.primary
            )

            // Unit sale price (if units > 0)
            if (produit.nombreUniteInt > 0) {
                val prixUnitVente = round((produit.prixVent / produit.nombreUniteInt) * 100.0) / 100.0
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

@Composable
private fun CardDroitPrixAchatEtBenVendeur(
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
    modifier: Modifier = Modifier
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
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
                textColor = MaterialTheme.colorScheme.tertiary
            )

            // Unit purchase price (if units > 0)
            if (produit.nombreUniteInt > 0) {
                val prixUnitAchat = round((produit.prixAchat / produit.nombreUniteInt) * 100.0) / 100.0
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
                    textColor = MaterialTheme.colorScheme.secondary
                )
            }

            // Price visibility toggle with improved styling
            FilledTonalButton(
                onClick = {
                    val updatedProduct = produit.copy(cachePrixVent = !produit.cachePrixVent)
                    updateProduct(updatedProduct)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (produit.cachePrixVent)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (produit.cachePrixVent) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (produit.cachePrixVent) "Caché" else "Visible",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium
                )
            }

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
                else MaterialTheme.colorScheme.error
            )
        }
    }
}
