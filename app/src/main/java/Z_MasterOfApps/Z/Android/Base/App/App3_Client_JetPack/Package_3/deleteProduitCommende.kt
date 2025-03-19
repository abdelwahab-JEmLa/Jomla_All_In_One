package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3

import Z_CodePartageEntreApps.Model.B_ClientsDataBase.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.SoldArticlesTabelle

fun deleteProduitCommende(
    viewModelInitApp: ViewModelInitApp,
    currentSale: SoldArticlesTabelle,
    currentClient: B_ClientsDataBase?
) {
    val produitsMainDataBase = viewModelInitApp.produitsMainDataBaseFromRepositeryPrototype
    produitsMainDataBase
        .find { it.id == currentSale.idArticle }?.let { product ->
            val indexOfFirst = product.bonsVentDeCetteCota.indexOfFirst {
                it.clientIdChoisi == currentClient?.id
            }

            // Remove the sale record
            produitsFireBaseRef
                .child(currentSale.idArticle.toString())
                .child("bonsVentDeCetteCotaList")
                .child(indexOfFirst.toString())
                .removeValue()

            // Create a list of color IDs and quantities to process
            val colorData = listOf(
                Pair(currentSale.color1IdPicked, currentSale.color1SoldQuantity),
                Pair(currentSale.color2IdPicked, currentSale.color2SoldQuantity),
                Pair(currentSale.color3IdPicked, currentSale.color3SoldQuantity),
                Pair(currentSale.color4IdPicked, currentSale.color4SoldQuantity)
            )

            // Process each color that has a quantity greater than 0
            colorData.forEach { (colorId, quantity) ->
                if (colorId > 0 && quantity > 0) {
                    product.bonCommendDeCetteCota?.coloursEtGoutsCommendeeList?.find {
                        it.id == colorId
                    }?.let { colorItem ->
                        val newQuantity = colorItem.quantityAchete - quantity

                        val colorIndex = product.bonCommendDeCetteCota!!.coloursEtGoutsCommendeeList.indexOfFirst {
                            it.id == colorId
                        }

                        if (colorIndex != -1) {
                            produitsFireBaseRef
                                .child(currentSale.idArticle.toString())
                                .child("bonCommendDeCetteCota")
                                .child("coloursEtGoutsCommendeeList")
                                .child(colorIndex.toString())
                                .child("quantityAchete")
                                .setValue(newQuantity)
                        }
                    }
                }
            }
        }
}
