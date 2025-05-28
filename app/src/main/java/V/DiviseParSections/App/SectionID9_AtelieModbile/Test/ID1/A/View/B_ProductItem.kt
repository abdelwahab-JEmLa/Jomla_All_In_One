package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    modifier: Modifier = Modifier,
    produitInit: A_ProduitInfosTest,
    onPrixUpdate: (A_ProduitInfosTest) -> Unit = {}
) {
    val TAG = "ProductItem"
    var produit by remember(produitInit.id, produitInit.actualiseSonImageTest2, produitInit.timestamps) {
        mutableStateOf(produitInit)
    }

    // Separate state for image refresh to force recomposition
    var imageRefreshKey by remember(produitInit.id) { mutableIntStateOf(0) }

    // Update product state when input changes
    LaunchedEffect(produitInit.actualiseSonImageTest2, produitInit.timestamps) {
        if (produitInit.id == produit.id &&
            (produitInit.actualiseSonImageTest2 != produit.actualiseSonImageTest2 ||
                    produitInit.timestamps != produit.timestamps)) {

            Log.d(TAG, "Updating product ${produitInit.nom}: refresh counter ${produit.actualiseSonImageTest2} -> ${produitInit.actualiseSonImageTest2}")
            produit = produitInit
            imageRefreshKey++ // Force image refresh
        }
    }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {

                    A_GlideDisplayImageByKeyId_Proto_5(
                        product=produit,
                        produitVID = produit.id,
                        refreshImage = produit.actualiseSonImageTest2,
                        size = 80.dp,
                    )

                    ColumnInfosBase(produit)

                    Column(horizontalAlignment = Alignment.End) {
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


                        UnitEditor(
                            currentUnits = produit.nombreUniteInt,
                            label = "Unités",
                            onUnitsUpdate = { newUnits ->
                                val updatedProduct = produit.copy(nombreUniteInt = newUnits)
                                produit = updatedProduct
                                onPrixUpdate(updatedProduct)
                            })

                        val prixUnitaire = if (produit.nombreUniteInt > 0) {
                            kotlin.math.round((produit.prixVent / produit.nombreUniteInt) * 100.0) / 100.0
                        } else {
                            0.0
                        }

                        if (produit.nombreUniteInt > 0) {
                            PriceEditor(
                                currentPrice = prixUnitaire,
                                label = "Prix/unité",
                                onPriceUpdate = { newPrixUnitaire ->
                                    val newPrixVent = newPrixUnitaire * produit.nombreUniteInt
                                    val updatedProduct = produit.copy(prixVent = newPrixVent)
                                    produit = updatedProduct
                                    onPrixUpdate(updatedProduct)
                                },
                                showOnlyWhenPositive = false,
                                textColor = MaterialTheme.colorScheme.secondary
                            )
                        }

                        PriceEditor(
                            currentPrice = produit.clientPrixVentUnite,
                            label = "Prix client/unité",
                            onPriceUpdate = { newClientPrixUnite ->
                                val updatedProduct =
                                    produit.copy(clientPrixVentUnite = newClientPrixUnite)
                                produit = updatedProduct
                                onPrixUpdate(updatedProduct)
                            },
                            showOnlyWhenPositive = false,
                            textColor = MaterialTheme.colorScheme.inversePrimary,
                            additionalInfo = {
                                val totalClientPrice = if (produit.nombreUniteInt > 0) {
                                    produit.clientPrixVentUnite * produit.nombreUniteInt
                                } else {
                                    0.0
                                }

                                if (produit.nombreUniteInt > 0) {
                                    Text(
                                        text = "Total client: $totalClientPrice DA",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            })

                        val prixAchatUnitaire = if (produit.nombreUniteInt > 0) {
                            kotlin.math.round((produit.prixAchat / produit.nombreUniteInt) * 100.0) / 100.0
                        } else {
                            0.0
                        }

                        if (produit.nombreUniteInt > 0) {
                            PriceEditor(
                                currentPrice = prixAchatUnitaire,
                                label = "Achat/unité",
                                onPriceUpdate = { newPrixAchatUnitaire ->
                                    val newPrixAchat = newPrixAchatUnitaire * produit.nombreUniteInt
                                    val updatedProduct = produit.copy(prixAchat = newPrixAchat)
                                    produit = updatedProduct
                                    onPrixUpdate(updatedProduct)
                                },
                                showOnlyWhenPositive = true,
                                textColor = MaterialTheme.colorScheme.tertiary
                            )
                        }
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

                                PriceEditor(
                                    currentPrice = benefice,
                                    label = "Bénéfice",
                                    onPriceUpdate = { newBenefice ->
                                        val newPrixVent = produit.prixAchat + newBenefice
                                        val updatedProduct = produit.copy(prixVent = newPrixVent)
                                        produit = updatedProduct
                                        onPrixUpdate(updatedProduct)
                                    },
                                    showOnlyWhenPositive = false, // Allow negative benefits (losses)
                                    textColor = if (benefice > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnInfosBase(produit: A_ProduitInfosTest) {
    Column() {
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
        // Debug info - remove this in production
        Text(
            text = "Refresh: ${produit.actualiseSonImageTest2}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
