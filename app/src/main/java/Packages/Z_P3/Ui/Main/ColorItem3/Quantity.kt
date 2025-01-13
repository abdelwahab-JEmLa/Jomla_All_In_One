package Packages.Z_P3.Ui.Main.ColorItem3

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.ViewModelInitApp
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
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
    indexColoreAcheter: Int,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>
) {
    Button(
        onClick = {
            onClick()

            // 1. Get current color ID based on index
            val currentColorId = currentSale?.let { sale ->
                when (indexColoreAcheter) {
                    0 -> sale.color1IdPicked
                    1 -> sale.color2IdPicked
                    2 -> sale.color3IdPicked
                    3 -> sale.color4IdPicked
                    else -> null
                }
            }

            // 2. Find color details and product
            val colorDetails = colorsArticlesTabelleModele.find { it.idColore == currentColorId }
            val product = currentSale?.let { sale ->
                viewModelInitApp._modelAppsFather.produitsMainDataBase.find { it.id == sale.idArticle }
            }

            // 3. Update product if found
            product?.let { currentProduct ->
                // Create bon commande if it doesn't exist
                if (currentProduct.bonCommendDeCetteCota == null) {
                    val timeNow = System.currentTimeMillis()
                    currentProduct.bonCommendDeCetteCota = _ModelAppsFather.ProduitModel.GrossistBonCommandes().apply {
                        vid = timeNow
                        grossistInformations = _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations(
                            id = timeNow,
                            nom = "Grossist_${timeNow % 1000}",
                            couleur = "#FF0000"
                        )
                    }
                }

                // Update color quantity
                colorDetails?.let { color ->
                    val existingColorEntry = currentProduct.bonCommendDeCetteCota?.coloursEtGoutsCommendee?.find {
                        it.nom == color.nameColore
                    }

                    if (existingColorEntry != null) {
                        existingColorEntry.quantityAchete += quantity
                    } else {
                        currentProduct.bonCommendDeCetteCota?.coloursEtGoutsCommendee?.add(
                            _ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
                                id = color.idColore,
                                nom = color.nameColore,
                                emoji = color.iconColore
                            ).apply {
                                quantityAchete = quantity
                            }
                        )
                    }

                    // Create new purchase model
                    val newPurchase = _ModelAppsFather.ProduitModel.ClientBonVentModel(
                        vid = currentSale.vid ?: System.currentTimeMillis()
                    ).apply {
                        // Set client information
                        currentClient?.let { client ->
                            clientInformations = _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                                id = client.vidSu,
                                nom = client.nomClientsSu,
                                couleur = client.couleurSu
                            )
                        }

                        // Add color purchase details
                        colours_Achete.add(
                            _ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel(
                                vidPosition = System.currentTimeMillis(),
                                nom = color.nameColore,
                                quantity_Achete = quantity,
                                imogi = color.iconColore
                            )
                        )
                    }

                    // Update or add sale
                    val existingSaleIndex = currentProduct.bonsVentDeCetteCota.indexOfFirst {
                        it.clientInformations?.id == currentClient?.vidSu
                    }

                    if (existingSaleIndex != -1) {
                        currentProduct.bonsVentDeCetteCota[existingSaleIndex] = newPurchase
                    } else {
                        currentProduct.bonsVentDeCetteCota.add(newPurchase)
                    }

                    // Update Firebase
                    _ModelAppsFather.updateProduit(currentProduct, viewModelInitApp)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = quantity.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
