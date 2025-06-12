package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class A_CentralDatasHandlerProtoJuin9(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val clientsState: B_ClientsState,
    val comptAppState: D_ComptAppState,
    val transactionCommercialState: C_TransactionCommercialState,
    val autreStates: Z_AutreStates,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    val clientOuSonMarqueMapEstOuvert by derivedStateOf {
        clientsState.findClientById(
            comptAppState.idClientOuSonMarqueMapEstOuvert
        )
    }

    val ouvertTransactionCommercial: C3_TransactionCommercial? by derivedStateOf {
        clientOuSonMarqueMapEstOuvert?.let {
            transactionCommercialState.getClientLastTransactionOnCommandActuellement(
                it.id
            )
        }
    }

    init {
        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _loadingProgress.floatValue = model.progress
                }
            }
        }
    }
}
