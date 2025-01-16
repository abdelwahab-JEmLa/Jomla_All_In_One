package Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Package_3

import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp

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
