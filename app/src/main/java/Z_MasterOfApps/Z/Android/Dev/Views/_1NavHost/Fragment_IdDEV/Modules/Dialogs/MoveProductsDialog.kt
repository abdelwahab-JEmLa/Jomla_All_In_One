package Z_MasterOfApps.Z.Android.Dev.Views._1NavHost.Fragment_IdDEV.Modules.Dialogs

import Z_MasterOfApps.Kotlin.Model.Extension.grossistsDisponible
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun MoveProductsDialog_F5(
    selectedProducts: List<_ModelAppsFather.ProduitModel>,    currentGrossist: _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations?,
    viewModelProduits: ViewModelInitApp,
    onDismiss: () -> Unit,
    onProductsMoved: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Move ${selectedProducts.size} products to another grossist",
                    style = MaterialTheme.typography.titleMedium
                )

                viewModelProduits._modelAppsFather.grossistsDisponible
                    .filter { it.id != currentGrossist?.id }
                    .forEach { grossist ->
                        Button(
                            onClick = {
                                selectedProducts.forEach { product ->
                                    product.bonCommendDeCetteCota?.let { bonCommande ->
                                        bonCommande.grossistInformations = grossist
                                        updateProduit(product, viewModelProduits)
                                    }
                                }
                                onDismiss()
                                onProductsMoved()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(grossist.nom)
                        }
                    }
            }
        }
    }
}
