package Z_MasterOfApps.Kotlin.ViewModel.Actions.F3_DisplayProductInfosToSeller

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle

class _F3_DisplayeProductInfosToSeller(val viewModelInitApp: ViewModelInitApp) {

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
