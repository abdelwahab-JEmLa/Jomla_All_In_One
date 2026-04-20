package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_4

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit

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
