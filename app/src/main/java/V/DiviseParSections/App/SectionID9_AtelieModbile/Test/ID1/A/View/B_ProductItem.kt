package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
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
                    // Prix de vente - now also editable using PriceEditor
                    PriceEditor(
                        currentPrice = produit.prixVent,
                        label = "Vente",
                        onPriceUpdate = { newPrix ->
                            val updatedProduct = produit.copy(prixVent = newPrix)
                            produit = updatedProduct
                            onPrixUpdate(updatedProduct)
                        },
                        showOnlyWhenPositive = false, // Always show sale price
                        textColor = MaterialTheme.colorScheme.primary
                    )

                    // Prix d'achat - using the modular component
                    MonPrixAchat(
                        produit = produit,
                        onPrixUpdate = { newPrix ->
                            val updatedProduct = produit.copy(monPrixAchat = newPrix)
                            produit = updatedProduct
                            onPrixUpdate(updatedProduct)
                        }
                    )
                }
            }
        }
    }
}
