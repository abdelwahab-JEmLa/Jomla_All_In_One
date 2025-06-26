package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem.Dialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ArticlesBasesStatsTable
import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem.QuantityButton
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ColorSelectionDialog(
    viewModel: VendeurAfficheurInfosProduitViewModel,
    viewModelInitApp: ViewModelInitApp,
    article: ArticlesBasesStatsTable,
    color: Int,
    compose_1_1_CouleurAcheteOperationVid: Long,
    onDismiss: () -> Unit,
    currentQuantity: Int,
    colorName: String,
    onQuantitySelected: (Int) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Dialog Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = colorName,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                // Quantity Grid
                QuantityGrid(
                    viewModel = viewModel,
                    currentQuantity = currentQuantity,
                    onQuantitySelected = { quantity ->
                        onQuantitySelected(quantity)
                        onDismiss()
                    },
                    viewModelInitApp = viewModelInitApp,
                    compose_1_1_CouleurAcheteOperationVid = compose_1_1_CouleurAcheteOperationVid,
                    article = article,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun QuantityGrid(
    viewModel: VendeurAfficheurInfosProduitViewModel,
    article: ArticlesBasesStatsTable,
    color: Int,

    currentQuantity: Int,
    onQuantitySelected: (Int) -> Unit,
    viewModelInitApp: ViewModelInitApp,
    compose_1_1_CouleurAcheteOperationVid: Long,
) {

    val quantities = remember {
        listOf(
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            20,
            21,
            22,
            23,
            24,
            25,
            30,
            40,
            50
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(1.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(240.dp)
    ) {
        items(quantities.size) { index ->
            val quantityNumber = quantities[index]
            QuantityButton(
                viewModel =viewModel,
                viewModelInitApp = viewModelInitApp,
                quantity = quantityNumber,
                isSelected = quantityNumber == currentQuantity,
                onClick = {
                    onQuantitySelected(quantityNumber)
                },
                compose_1_1_CouleurAcheteOperationVid =compose_1_1_CouleurAcheteOperationVid,
                article =article, colorIndex = color
            )
        }
    }
}

