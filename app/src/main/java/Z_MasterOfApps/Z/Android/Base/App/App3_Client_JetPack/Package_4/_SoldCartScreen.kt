package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_4

import V.DiviseParSections.App.Shared.Repository.MID2ClientRepository.Repository.HClientInfos
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable

class _SoldCartScreen(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        colorIndex: Int,
        article: ArticlesBasesStatsTable,
        clientBuyerNow: HClientInfos
    ) {
        deleteColore(viewModelInitApp,colorIndex, article, clientBuyerNow)
    }
}
