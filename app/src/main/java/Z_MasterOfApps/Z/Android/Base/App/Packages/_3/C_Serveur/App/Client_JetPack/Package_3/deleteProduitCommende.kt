package Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Package_3

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.produitsFireBaseRef
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.SoldArticlesTabelle

fun deleteProduitCommende(
    viewModelInitApp: ViewModelInitApp,
    currentSale: SoldArticlesTabelle,
    currentClient: ClientsModel?
) {
    viewModelInitApp._modelAppsFather.produitsMainDataBase
        .removeIf { it.id == currentSale.idArticle && it.itsTempProduit }
        .also {
            produitsFireBaseRef
                .child(currentSale.idArticle.toString())
                .removeValue()
        }
    // Find the product and update it
    viewModelInitApp._modelAppsFather.produitsMainDataBase
        .find { it.id == currentSale.idArticle }?.let { product ->

            product.bonsVentDeCetteCota
                .removeIf { bonsVent ->
                    bonsVent.clientInformations?.id == currentClient?.idClientsSu
                }.also {
                    produitsFireBaseRef
                        .child(currentSale.idArticle.toString())
                        .child("bonsVentDeCetteCota")
                        .removeValue()
                }

            _ModelAppsFather.updateProduit(product, viewModelInitApp)
        }
}
