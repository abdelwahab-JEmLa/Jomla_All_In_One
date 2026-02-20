package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_4

import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import EntreApps.Shared.Models.M01Produit

class _SoldCartScreen(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        colorIndex: Int,
        article: M01Produit,
        clientBuyerNow: M2Client
    ) {
        deleteColore(viewModelInitApp,colorIndex, article, clientBuyerNow)
    }
}
