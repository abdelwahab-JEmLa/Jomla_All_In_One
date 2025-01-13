package Packages.Z_P3.Ui.Main.ColorItem3

import Y_AppsFather.Kotlin.ModelAppsFather
import Y_AppsFather.Kotlin.ViewModelInitApp
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
 fun ColorSelectionDialog(
    onDismiss: () -> Unit,
    currentQuantity: Int,
    colorName: String,
    onQuantitySelected: (Int) -> Unit,
    currentSale: SoldArticlesTabelle?,
    viewModelInitApp: ViewModelInitApp, currentClient: ClientsModel?, indexColoreAcheter: Int,
     colorsArticlesTabelleModele: List<ColorsArticlesTabelle>
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
                    currentSale =currentSale,
                    viewModelInitApp =viewModelInitApp,
                    currentClient,indexColoreAcheter,  colorsArticlesTabelleModele = colorsArticlesTabelleModele
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
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>
) {
    val quantities = remember {
        listOf(0,1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14, 15, 20, 21, 22, 23,24, 25, 30, 40, 50)
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
                currentClient, indexColoreAcheter,  colorsArticlesTabelleModele
            )
        }
    }
}

@Composable
private fun QuantityButton(
    quantity: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    currentSale: SoldArticlesTabelle?,
    viewModelInitApp: ViewModelInitApp,
    currentClient: ClientsModel?,
    indexColoreAcheter: Int,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>
) {
    Button(
        onClick = {
            onClick()
            // Find the current color based on index
            val currentColorId = when (indexColoreAcheter) {
                0 -> currentSale?.color1IdPicked
                1 -> currentSale?.color2IdPicked
                2 -> currentSale?.color3IdPicked
                3 -> currentSale?.color4IdPicked
                else -> null
            }

            // Find the corresponding color details
            val colorDetails = colorsArticlesTabelleModele.find { it.idColore == currentColorId }

            // Create new purchase model
            val newPurchase = ModelAppsFather.ProduitModel.ClientBonVentModel(
                vid = currentSale?.vid ?: System.currentTimeMillis()
            ).apply {
                // Set client information if available
                if (currentClient != null) {
                    clientInformations = ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = currentClient.vidSu,
                        nom = currentClient.nomClientsSu,
                        couleur = currentClient.couleurSu
                    )
                }

                // Add color purchase details
                colorDetails?.let { color ->
                    colours_Achete.add(
                        ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel(
                            vidPosition = System.currentTimeMillis(),
                            nom = color.nameColore,
                            quantity_Achete = quantity,
                            imogi = color.iconColore
                        )
                    )
                }
            }

            // Update the database
            if (currentSale != null) {
                viewModelInitApp._modelAppsFather.produitsMainDataBase
                    .find { it.id == currentSale.vid }?.let { product ->
                        // Find and update existing sale or add new one
                        val existingSaleIndex = product.bonsVentDeCetteCota.indexOfFirst {
                            it.clientInformations?.id == currentClient?.vidSu
                        }
                        if (existingSaleIndex != -1) {
                            product.bonsVentDeCetteCota[existingSaleIndex] = newPurchase
                        } else {
                            product.bonsVentDeCetteCota.add(newPurchase)
                        }

                        // Update Firebase
                        ModelAppsFather.updateProduct_produitsAvecBonsGrossist(product, viewModelInitApp)
                    }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = quantity.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
