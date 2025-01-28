package Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Package_3

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.SoldArticlesTabelle

class _DisplayeProductInfosToSeller(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        currentSale: SoldArticlesTabelle,
        currentClient: ClientsModel?
    ) {
        deleteProduitCommende(viewModelInitApp, currentSale, currentClient)
    }

    fun onClickComposeQuantityButton(
        quantity: Int,
        currentSale: SoldArticlesTabelle?,
        currentClient: ClientsModel?,
        colorDetails: ColorsArticlesTabelle
    ) {
        calQuantityButton(
            quantity,
            currentSale,
            currentClient,
            colorDetails,
            viewModelInitApp
        )
    }
}
