package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun ProductItem(
    produitInit: A_ProduitInfosTest,
    onPrixUpdate: (A_ProduitInfosTest) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var produit by remember { mutableStateOf(produitInit) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = produit.nom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${produit.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    // Prix de vente - editable using PriceEditor
                    PriceEditor(
                        currentPrice = produit.prixVent,
                        label = "Vente",
                        onPriceUpdate = { newPrix ->
                            val updatedProduct = produit.copy(prixVent = newPrix)
                            produit = updatedProduct
                            onPrixUpdate(updatedProduct)
                        },
                        showOnlyWhenPositive = false,
                        textColor = MaterialTheme.colorScheme.primary
                    )

                    // Nombre d'unités - editable using UnitEditor
                    UnitEditor(
                        currentUnits = produit.nombreUniteInt,
                        label = "Unités",
                        onUnitsUpdate = { newUnits ->
                            val updatedProduct = produit.copy(nombreUniteInt = newUnits)
                            produit = updatedProduct
                            onPrixUpdate(updatedProduct)
                        }
                    )

                    // Prix unitaire - editable and updates sale price
                    val prixUnitaire = if (produit.nombreUniteInt > 0) {
                        String.format("%.2f", produit.prixVent / produit.nombreUniteInt).toDouble()
                    } else {
                        0.0
                    }

                    if (produit.nombreUniteInt > 0) {
                        PriceEditor(
                            currentPrice = prixUnitaire,
                            label = "Prix/unité",
                            onPriceUpdate = { newPrixUnitaire ->
                                // Calculate new sale price based on unit price
                                val newPrixVent = newPrixUnitaire * produit.nombreUniteInt
                                val updatedProduct = produit.copy(prixVent = newPrixVent)
                                produit = updatedProduct
                                onPrixUpdate(updatedProduct)
                            },
                            showOnlyWhenPositive = false,
                            textColor = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Prix d'achat - editable using PriceEditor with benefit calculation
                    PriceEditor(
                        currentPrice = produit.prixAchat,
                        label = "Achat",
                        onPriceUpdate = { newPrix ->
                            val updatedProduct = produit.copy(prixAchat = newPrix)
                            produit = updatedProduct
                            onPrixUpdate(updatedProduct)
                        },
                        showOnlyWhenPositive = true,
                        additionalInfo = {
                            val benefice = produit.prixVent - produit.prixAchat
                            Text(
                                text = "Bénéfice: $benefice DA",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (benefice > 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}
