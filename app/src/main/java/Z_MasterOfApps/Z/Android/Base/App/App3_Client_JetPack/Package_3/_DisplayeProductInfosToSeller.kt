package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle

class _DisplayeProductInfosToSeller(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        currentSale: SoldArticlesTabelle,
        currentClient: B_ClientInfosProtoJuin3?
    ) {
        deleteProduitCommende(viewModelInitApp, currentSale, currentClient)
    }

    fun onClickComposeQuantityButton(
        quantity: Int,
        currentSale: SoldArticlesTabelle?,
        currentClient: B_ClientInfosProtoJuin3?,
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
