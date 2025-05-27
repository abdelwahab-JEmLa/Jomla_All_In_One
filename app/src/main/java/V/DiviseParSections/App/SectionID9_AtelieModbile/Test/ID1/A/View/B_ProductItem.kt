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
                    // FIX: Calculate directly as Double and round to 2 decimal places
                    val prixUnitaire = if (produit.nombreUniteInt > 0) {
                        // Round to 2 decimal places: multiply by 100, round, then divide by 100
                        kotlin.math.round((produit.prixVent / produit.nombreUniteInt) * 100.0) / 100.0
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

                    // Prix d'achat unitaire - editable and updates purchase price
                    val prixAchatUnitaire = if (produit.nombreUniteInt > 0) {
                        // Round to 2 decimal places
                        kotlin.math.round((produit.prixAchat / produit.nombreUniteInt) * 100.0) / 100.0
                    } else {
                        0.0
                    }

                    if (produit.nombreUniteInt > 0) {
                        PriceEditor(
                            currentPrice = prixAchatUnitaire,
                            label = "Achat/unité",
                            onPriceUpdate = { newPrixAchatUnitaire ->
                                // Calculate new purchase price based on unit price
                                val newPrixAchat = newPrixAchatUnitaire * produit.nombreUniteInt
                                val updatedProduct = produit.copy(prixAchat = newPrixAchat)
                                produit = updatedProduct
                                onPrixUpdate(updatedProduct)
                            },
                            showOnlyWhenPositive = true,
                            textColor = MaterialTheme.colorScheme.tertiary
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

                            // Editable benefit using PriceEditor
                            PriceEditor(
                                currentPrice = benefice,
                                label = "Bénéfice",
                                onPriceUpdate = { newBenefice ->
                                    // Calculate new sale price based on desired benefit
                                    val newPrixVent = produit.prixAchat + newBenefice
                                    val updatedProduct = produit.copy(prixVent = newPrixVent)
                                    produit = updatedProduct
                                    onPrixUpdate(updatedProduct)
                                },
                                showOnlyWhenPositive = false, // Allow negative benefits (losses)
                                textColor = if (benefice > 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}
