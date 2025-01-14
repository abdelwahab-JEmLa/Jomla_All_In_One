package Packages.Z_P3.Ui.Main.ColorItem3

import Y_AppsFather.Kotlin.Model._ModelAppsFather
import Y_AppsFather.Kotlin.Model._ModelAppsFather.Companion.createNewProduct
import Y_AppsFather.Kotlin.ViewModel.ViewModelInitApp
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
    if (currentSale?.clientSoldToItId == colorDetails.idColore) {
        handleQuantitySelection(
            1,
            currentSale,
            currentClient,
            colorDetails,
            viewModelInitApp
        )
    }

    Button(
        onClick = {
            onClick()
            handleQuantitySelection(
                quantity = quantity,
                currentSale = currentSale,
                currentClient = currentClient,
                colorDetails = colorDetails,
                viewModelInitApp = viewModelInitApp
            )
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

fun handleQuantitySelection(
    quantity: Int,
    currentSale: SoldArticlesTabelle?,
    currentClient: ClientsModel?,
    colorDetails: ColorsArticlesTabelle,
    viewModelInitApp: ViewModelInitApp
) {
    try {
        // Find or create product
        val product = viewModelInitApp._modelAppsFather.produitsMainDataBase
            .find { it.id == currentSale?.idArticle }
            ?: createNewProduct(viewModelInitApp, currentSale?.nameArticle!!)

        // Create or update color purchase
        val colorPurchase = _ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel(
            couleurId = colorDetails.idColore,
            nom = colorDetails.nameColore,
            quantity_Achete = quantity,
            imogi = colorDetails.iconColore
        )

        // Get or create sale
        val existingSaleIndex = product.bonsVentDeCetteCota
            .indexOfFirst { it.clientInformations?.id == currentClient?.idClientsSu }

        if (existingSaleIndex != -1) {
            // Update existing sale
            val existingSale = product.bonsVentDeCetteCota[existingSaleIndex]
            val colorIndex = existingSale.colours_Achete
                .indexOfFirst { it.couleurId == colorDetails.idColore }

            if (colorIndex != -1) {
                existingSale.colours_Achete[colorIndex] = colorPurchase
            } else {
                existingSale.colours_Achete.add(colorPurchase)

            }
        } else {
            // Create new sale
            val newSale = _ModelAppsFather.ProduitModel.ClientBonVentModel(
                vid = currentSale?.vid ?: System.currentTimeMillis()
            ).apply {
                clientInformations = currentClient?.let {
                    _ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = it.idClientsSu,
                        nom = it.nomClientsSu,
                        couleur = it.couleurSu
                    ).apply {
                        positionDonClientsList = 0
                        auFilterFAB = false
                    }
                }
                colours_Achete.add(colorPurchase)
            }
            product.bonCommendDeCetteCota

            product.bonsVentDeCetteCota.add(newSale)

        }

        _ModelAppsFather.updateProduit(product, viewModelInitApp)
        product.updateBonCommande()

    } catch (e: Exception) {
        Log.e("QuantityButton", "Error updating sale", e)
    }
}


