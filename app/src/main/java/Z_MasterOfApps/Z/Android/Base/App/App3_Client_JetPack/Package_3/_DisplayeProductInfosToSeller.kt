package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3

import EntreApps.Shared.Models.M2Client
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp

class _DisplayeProductInfosToSeller(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        currentSale: SoldArticlesTabelle,
        currentClient: M2Client?
    ) {
        deleteProduitCommende(viewModelInitApp, currentSale, currentClient)
    }

    fun onClickComposeQuantityButton(
        quantity: Int,
        currentSale: SoldArticlesTabelle?,
        currentClient: M2Client?,
        colorDetails: ColorsArticlesTabelle
    ) {
        updateNewModelDeLachat(
            quantity,
            currentSale,
            currentClient,
            colorDetails,
            viewModelInitApp
        )
    }
}
