package Packages.Z_P3.Ui.Main.ColorItem3

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.ViewModelInitApp
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import android.util.Log
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantityButton(
    quantity: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    currentSale: SoldArticlesTabelle?,
    viewModelInitApp: ViewModelInitApp,
    currentClient: ClientsModel?,
    colorDetails: ColorsArticlesTabelle
) {
    Button(
        onClick = {
            onClick()
            // Create new purchase model with proper initialization
            val newPurchase = _ModelAppsFather.ProduitModel.ClientBonVentModel(
                vid = currentSale?.vid ?: System.currentTimeMillis()
            ).apply {
                // Set client information if available
                if (currentClient != null) {
                    clientInformations = _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = currentClient.vidSu,
                        nom = currentClient.nomClientsSu,
                        couleur = currentClient.couleurSu
                    ).apply {
                        positionDonClientsList = 0 // Set default position
                        auFilterFAB = false // Set default filter state
                    }
                }

                // Add color purchase details with proper initialization
                colorDetails?.let { color ->
                    colours_Achete.add(
                        _ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel(
                            couleurId = color.idColore,
                            nom = color.nameColore,
                            quantity_Achete = quantity,
                            imogi = color.iconColore
                        )
                    )
                }
            }

            // Update the database with proper error handling
            if (currentSale != null) {
                viewModelInitApp._modelAppsFather.produitsMainDataBase
                    .find { it.id == currentSale.idArticle }?.let { product ->
                        try {
                            // Update or add the sale
                            val existingSaleIndex = product.bonsVentDeCetteCota.indexOfFirst {
                                it.clientInformations?.id == currentClient?.vidSu
                            }

                            if (existingSaleIndex != -1) {
                                // Update existing sale
                                val existingSale = product.bonsVentDeCetteCota[existingSaleIndex]
                                val updatedSale = existingSale.apply {
                                    // Update existing color or add new one
                                    val colorIndex = colours_Achete.indexOfFirst {
                                        it.couleurId == colorDetails?.idColore
                                    }
                                    if (colorIndex != -1) {
                                        colours_Achete[colorIndex] = _ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel(
                                            couleurId = colorDetails?.idColore?: 0,
                                            nom = colorDetails?.nameColore ?: "",
                                            quantity_Achete = quantity,
                                            imogi = colorDetails?.iconColore ?: ""
                                        )
                                    } else {
                                        colours_Achete.add(
                                            _ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel(
                                                couleurId =colorDetails?.idColore ?: 0,
                                                nom = colorDetails?.nameColore ?: "",
                                                quantity_Achete = quantity,
                                                imogi = colorDetails?.iconColore ?: ""
                                            )
                                        )
                                    }
                                }
                                product.bonsVentDeCetteCota[existingSaleIndex] = updatedSale
                            } else {
                                // Add new sale
                                product.bonsVentDeCetteCota.add(newPurchase)
                            }

                            // Update Firebase and recalculate
                            _ModelAppsFather.updateProduit(product, viewModelInitApp)
                            _ModelAppsFather.ProduitModel.GrossistBonCommandes.calculeSelf(
                                viewModelInitApp,
                                product
                            )
                        } catch (e: Exception) {
                            Log.e("QuantityButton", "Error updating sale", e)
                            // Handle error appropriately
                        }
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
