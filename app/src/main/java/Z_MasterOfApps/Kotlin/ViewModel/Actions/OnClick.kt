package Z_MasterOfApps.Kotlin.ViewModel.Actions

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.createNewProduct
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import android.util.Log

object onClick {
    fun onClickQuantityButton(
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
                val newSale = ClientBonVentModel(
                    vid = currentSale?.vid ?: System.currentTimeMillis()
                ).apply {
                    clientInformations = currentClient?.let {
                        ClientBonVentModel.ClientInformations(
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

                product.bonsVentDeCetteCota.add(newSale)

            }


            _ModelAppsFather.updateProduit(product, viewModelInitApp)
            //   product.updateBonCommande()

        } catch (e: Exception) {
            Log.e("QuantityButton", "Error updating sale", e)
        }
    }
}

