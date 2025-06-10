package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.Repository

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
class A_CentralDatasHandler(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val clientsState: B_ClientsState,
    val comptAppState: D_ComptAppState,
    val transactionCommercialState: C_TransactionCommercialState,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    init {
        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _loadingProgress.floatValue = model.progress
                }
            }
        }
    }

    val ouvertTransactionCommercial: C3_TransactionCommercial? by derivedStateOf {
        transactionCommercialState.datas.value.lastOrNull {
            it.clientAcheteurID == comptAppState.activeClientPourCeCompt
                    && it.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        }
    }

    val ouvertClient by derivedStateOf {
        clientsState.findClientById(comptAppState.activeClientPourCeCompt)
    }
}
