package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.UnitEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonDefaults
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
    onShowDetailsExpandedChange: (Boolean) -> Unit,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    // Expandable Details Section
    FilledTonalButton(
        onClick = { onShowDetailsExpandedChange(!showDetailsExpanded) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (showDetailsExpanded) "Masquer les détails" else "Afficher les détails",
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = if (showDetailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }

    // Detailed Editing Section (Expandable)
    if (showDetailsExpanded) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Prix Section
                Text(
                    text = "💰 Prix et Calculs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left column
                    Column(modifier = Modifier.weight(1f)) {
                        PriceEditor(
                            currentPrice = produit.prixVent,
                            label = "Prix Vente Total",
                            onPriceUpdate = { newPrix ->
                                updateProduct(produit.copy(prixVent = newPrix))
                            },
                            textColor = MaterialTheme.colorScheme.primary
                        )

                        if (produit.nombreUniteInt > 0) {
                            val prixUnitVente =
                                round((produit.prixVent / produit.nombreUniteInt) * 100.0) / 100.0
                            PriceEditor(
                                currentPrice = prixUnitVente,
                                label = "Vente/unité",
                                onPriceUpdate = { newPrixUnit ->
                                    val newPrixVent = newPrixUnit * produit.nombreUniteInt
                                    updateProduct(produit.copy(prixVent = newPrixVent))
                                },
                                textColor = MaterialTheme.colorScheme.secondary
                            )
                        }

                        PriceEditor(
                            currentPrice = produit.prixAchat,
                            label = "Prix Achat Total",
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
                    }

                    // Right column
                    Column(modifier = Modifier.weight(1f)) {
                        UnitEditor(
                            currentUnits = produit.nombreUniteInt,
                            label = "Nombre Unités",
                            onUnitsUpdate = { newUnits ->
                                updateProduct(produit.copy(nombreUniteInt = newUnits))
                            }
                        )

                        if (produit.nombreUniteInt > 0) {
                            PriceEditor(
                                currentPrice = produit.clientPrixVentUnite,
                                label = "Prix Client/unité",
                                onPriceUpdate = { newClientPrixUnite ->
                                    updateProduct(produit.copy(clientPrixVentUnite = newClientPrixUnite))
                                },
                                textColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        }

                        val benefice = produit.prixVent - produit.prixAchat
                        PriceEditor(
                            currentPrice = benefice,
                            label = "Bénéfice Total",
                            onPriceUpdate = { newBenefice ->
                                val newPrixVent = produit.prixAchat + newBenefice
                                updateProduct(produit.copy(prixVent = newPrixVent))
                            },
                            textColor = if (benefice > 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            val updatedProduct =
                                produit.copy(cachePrixVent = !produit.cachePrixVent)
                            updateProduct(updatedProduct)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (produit.cachePrixVent)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (produit.cachePrixVent) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (produit.cachePrixVent) "Prix Caché" else "Prix Visible",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            val updatedProduct =
                                produit.copy(heldPrioriteDemandAuGrossist = !produit.heldPrioriteDemandAuGrossist)
                            updateProduct(updatedProduct)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (produit.heldPrioriteDemandAuGrossist)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (produit.heldPrioriteDemandAuGrossist) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Priorité",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
