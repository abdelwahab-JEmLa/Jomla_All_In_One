package Z_MasterOfApps.Kotlin.ViewModel.Actions.Package_4

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.ClientsModel

class _SoldCartScreen(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        colorIndex: Int,
        article: ArticlesBasesStatsTable,
        clientBuyerNow: ClientsModel
    ) {
        deleteColore(viewModelInitApp,colorIndex, article, clientBuyerNow)
    }
}
