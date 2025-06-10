package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.ViewModel

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import androidx.lifecycle.ViewModel


class PanierFinaleDAchatViewModel(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,

    val centralDatasHandler: CentralDatasHandler,
    val comptAppState: ComptAppState,
    val transactionCommercialState: TransactionCommercialState,
) : ViewModel() {
    private val TAG = "PanierFinaleDAchatViewModel"


}
