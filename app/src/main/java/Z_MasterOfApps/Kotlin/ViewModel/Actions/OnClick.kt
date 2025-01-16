package Z_MasterOfApps.Kotlin.ViewModel.Actions

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
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
    try {
        LogUtils.logQuantity("Starting quantity update - Quantity: $quantity, Color: ${colorDetails.nameColore}")

        // Early return if required data is missing
        if (currentSale == null || currentClient == null) {
            LogUtils.logError(LogUtils.Tags.QUANTITY_BUTTON, "Required sale or client data is null")
            return
        }

        // Find and validate product
        val productIndex = viewModelInitApp._modelAppsFather.produitsMainDataBase
            .indexOfFirst { it.id == currentSale.idArticle }
        val product = viewModelInitApp._modelAppsFather.produitsMainDataBase.getOrNull(productIndex) ?: return

        // Create color purchase model
        val colorPurchase = ClientBonVentModel.ColorAchatModel(
            vidPosition = System.currentTimeMillis(),
            couleurId = colorDetails.idColore,
            nom = colorDetails.nameColore,
            quantity_Achete = quantity,
            imogi = colorDetails.iconColore
        )

        // Find or create client sale
        val existingSaleIndex = product.bonsVentDeCetteCota
            .indexOfFirst { it.clientInformations?.id == currentClient.idClientsSu }

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
            product.bonsVentDeCetteCota.add(ClientBonVentModel(
                vid = System.currentTimeMillis()
            ).apply {
                clientInformations = ClientBonVentModel.ClientInformations(
                    id = currentClient.idClientsSu,
                    nom = currentClient.nomClientsSu,
                    couleur = currentClient.couleurSu
                )
                colours_Achete.add(colorPurchase)
            })
        }

        // Update state and Firebase
        viewModelInitApp.viewModelScope.launch {
            try {
                // Mise à jour du bon commande
                if (product.bonCommendDeCetteCota == null && product.bonsVentDeCetteCota.isNotEmpty()) {
                    // Créer un nouveau bon commande
                    product.bonCommendDeCetteCota = GrossistBonCommandes(
                        vid = System.currentTimeMillis(),
                        date = java.time.LocalDateTime.now().toString(),
                        init_grossistInformations = product.historiqueBonsCommend.lastOrNull()?.grossistInformations,
                        init_coloursEtGoutsCommendee = product.bonsVentDeCetteCota
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
                    )
                }

                // Update local and Firebase
                viewModelInitApp._modelAppsFather.produitsMainDataBase[productIndex] = product.calculeSelfGrossistBonCommandesExtension()

                _ModelAppsFather.updateProduit(product, viewModelInitApp)

            } catch (e: Exception) {
                LogUtils.logError(LogUtils.Tags.BON_COMMANDES, "Error updating state", e)
            }
        }

    } catch (e: Exception) {
        LogUtils.logError(LogUtils.Tags.QUANTITY_BUTTON, "Error updating sale", e)
    }
}
