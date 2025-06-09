package Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_4

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable

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
