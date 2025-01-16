package Z_MasterOfApps.Kotlin.ViewModel.Actions

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.Companion.calculeSelfGrossistBonCommandesExtension
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.GrossistBonCommandes
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun onClickQuantityButton(
    quantity: Int,
    currentSale: SoldArticlesTabelle?,
    currentClient: ClientsModel?,
    colorDetails: ColorsArticlesTabelle,
    viewModelInitApp: ViewModelInitApp
) {
    try { //-->
    //TODO(1): pk au premier inseration bonCommend est null au 2eme ca marche 
        LogUtils.logQuantity("Starting quantity update - Quantity: $quantity, Color: ${colorDetails.nameColore}")

        if (currentSale == null || currentClient == null) {
            LogUtils.logError(LogUtils.Tags.QUANTITY_BUTTON, "Required sale or client data is null")
            return
        }

        // Find product in database
        val productIndex = viewModelInitApp._modelAppsFather.produitsMainDataBase
            .indexOfFirst { it.id == currentSale.idArticle }

        val product = viewModelInitApp._modelAppsFather.produitsMainDataBase.getOrNull(productIndex)
            ?: return

        LogUtils.logProductState(product)

        // Create or update client sale
        updateClientSale(
            product = product,
            client = currentClient,
            colorDetails = colorDetails,
            quantity = quantity
        )

        // Update UI and Firebase
        updateProductState(
            product = product,
            productIndex = productIndex,
            viewModelInitApp = viewModelInitApp
        )

    } catch (e: Exception) {
        LogUtils.logError(LogUtils.Tags.QUANTITY_BUTTON, "Error updating sale", e)
    }
}

private fun updateClientSale(
    product: ProduitModel,
    client: ClientsModel,
    colorDetails: ColorsArticlesTabelle,
    quantity: Int
) {
    val colorPurchase = ClientBonVentModel.ColorAchatModel(
        vidPosition = System.currentTimeMillis(),
        couleurId = colorDetails.idColore,
        nom = colorDetails.nameColore,
        quantity_Achete = quantity,
        imogi = colorDetails.iconColore
    )

    // Find existing sale for client
    val existingSaleIndex = product.bonsVentDeCetteCota
        .indexOfFirst { it.clientInformations?.id == client.idClientsSu }

    if (existingSaleIndex != -1) {
        // Update existing sale
        updateExistingSale(
            product.bonsVentDeCetteCota[existingSaleIndex],
            colorDetails.idColore,
            colorPurchase
        )
    } else {
        // Create new sale
        createNewSale(product, client, colorPurchase)
    }
}

private fun updateExistingSale(
    existingSale: ClientBonVentModel,
    colorId: Long,
    colorPurchase: ClientBonVentModel.ColorAchatModel
) {
    val colorIndex = existingSale.colours_Achete
        .indexOfFirst { it.couleurId == colorId }

    if (colorIndex != -1) {
        // Update existing color quantity
        existingSale.colours_Achete[colorIndex] = colorPurchase
    } else {
        // Add new color
        existingSale.colours_Achete.add(colorPurchase)
    }
}

private fun createNewSale(
    product: ProduitModel,
    client: ClientsModel,
    colorPurchase: ClientBonVentModel.ColorAchatModel
) {
    val newSale = ClientBonVentModel(
        vid = System.currentTimeMillis()
    ).apply {
        clientInformations = ClientBonVentModel.ClientInformations(
            id = client.idClientsSu,
            nom = client.nomClientsSu,
            couleur = client.couleurSu
        )
        colours_Achete.add(colorPurchase)
    }
    product.bonsVentDeCetteCota.add(newSale)
}

private fun updateProductState(
    product: ProduitModel,
    productIndex: Int,
    viewModelInitApp: ViewModelInitApp
) {
    // Update UI first
    viewModelInitApp._modelAppsFather.produitsMainDataBase[productIndex] = product

    // Calculate bon commande immediately using the extension function
    product.calculeSelfGrossistBonCommandesExtension()

    // Update Firebase in background
    viewModelInitApp.viewModelScope.launch {
        try {
            LogUtils.logBonCommandes("Starting Firebase update for product ${product.id}")

            // Update Firebase with the already calculated state
            _ModelAppsFather.updateProduit(product, viewModelInitApp)
            LogUtils.logBonCommandes("Firebase update completed")

        } catch (e: Exception) {
            LogUtils.logError(LogUtils.Tags.BON_COMMANDES, "Error updating Firebase", e)
        }
    }
}

private fun calculateBonCommande(product: ProduitModel): GrossistBonCommandes? {
    if (product.bonsVentDeCetteCota.isEmpty()) {
        return null
    }

    return GrossistBonCommandes(
        vid = System.currentTimeMillis(),
        date = java.time.LocalDateTime.now().toString(),
        init_grossistInformations = product.bonCommendDeCetteCota?.grossistInformations
            ?: product.historiqueBonsCommend.lastOrNull()?.grossistInformations,
        init_coloursEtGoutsCommendee = aggregateColorQuantities(product)
    )
}

private fun aggregateColorQuantities(product: ProduitModel): List<GrossistBonCommandes.ColoursGoutsCommendee> {
    return product.bonsVentDeCetteCota
        .flatMap { it.colours_Achete }
        .groupBy { it.couleurId }
        .mapNotNull { (couleurId, colorList) ->
            colorList.firstOrNull()?.let { firstColor ->
                val totalQuantity = colorList.sumOf { it.quantity_Achete }
                if (totalQuantity > 0) {
                    GrossistBonCommandes.ColoursGoutsCommendee(
                        id = couleurId,
                        nom = firstColor.nom,
                        emogi = firstColor.imogi
                    ).apply {
                        quantityAchete = totalQuantity
                    }
                } else null
            }
        }
}


