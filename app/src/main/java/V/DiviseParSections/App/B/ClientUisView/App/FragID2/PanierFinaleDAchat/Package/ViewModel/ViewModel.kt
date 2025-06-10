package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.Repository.A_CentralDatasHandlerProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.Repository.D_ComptAppState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.Repository.C_TransactionCommercialState
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import androidx.lifecycle.ViewModel


class PanierFinaleDAchatViewModel(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,

    val centralDatasHandler: A_CentralDatasHandlerProtoJuin9,
    val comptAppState: D_ComptAppState,
    val transactionCommercialState: C_TransactionCommercialState,
) : ViewModel() {
    private val TAG = "PanierFinaleDAchatViewModel"


}
