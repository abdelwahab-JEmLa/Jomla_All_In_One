package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_4

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable

class _SoldCartScreen(val viewModelInitApp: ViewModelInitApp) {
    fun onClickOnMain(
        viewModelInitApp: ViewModelInitApp,
        colorIndex: Int,
        article: ArticlesBasesStatsTable,
        clientBuyerNow: B_ClientInfosProtoJuin3
    ) {
        deleteColore(viewModelInitApp,colorIndex, article, clientBuyerNow)
    }
}
