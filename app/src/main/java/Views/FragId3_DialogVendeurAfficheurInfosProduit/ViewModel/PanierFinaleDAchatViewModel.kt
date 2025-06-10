package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.Repository.A_CentralDatasHandler
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.Repository.C_TransactionCommercialState
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.Repository.D_ComptAppState
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import androidx.lifecycle.ViewModel


class VendeurAfficheurInfosProduitViewModel(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,

    val centralDatasHandler: A_CentralDatasHandler,
    val comptAppState: D_ComptAppState,
    val transactionCommercialState: C_TransactionCommercialState,
) : ViewModel() {
    private val TAG = "PanierFinaleDAchatViewModel"


}
