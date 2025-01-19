package Views.Z_P3.Ui.Main.ColorItem3

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.SoldArticlesTabelle
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
    onDismiss: () -> Unit,
    currentQuantity: Int,
    colorName: String,
    onQuantitySelected: (Int) -> Unit,
    currentSale: SoldArticlesTabelle?,
    viewModelInitApp: ViewModelInitApp,
    currentClient: ClientsModel?,
    indexColoreAcheter: Int,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
    color: ColorsArticlesTabelle
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
                    currentQuantity = currentQuantity,
                    onQuantitySelected = { quantity ->
                        onQuantitySelected(quantity)
                        onDismiss()
                    },
                    currentSale = currentSale,
                    viewModelInitApp = viewModelInitApp,
                    currentClient = currentClient,
                    indexColoreAcheter = indexColoreAcheter,
                    colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                    color = color
                )
            }
        }
    }
}
@Composable
private fun QuantityGrid(
    currentQuantity: Int,
    onQuantitySelected: (Int) -> Unit,
    currentSale: SoldArticlesTabelle?,
    viewModelInitApp: ViewModelInitApp,
    currentClient: ClientsModel?,
    indexColoreAcheter: Int,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
    color: ColorsArticlesTabelle
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
            val quantity = quantities[index]
            QuantityButton(
                quantity = quantity,
                isSelected = quantity == currentQuantity,
                onClick = {
                    onQuantitySelected(quantity)

                },
                currentSale,
                viewModelInitApp,
                currentClient, color
            )
        }
    }
}

