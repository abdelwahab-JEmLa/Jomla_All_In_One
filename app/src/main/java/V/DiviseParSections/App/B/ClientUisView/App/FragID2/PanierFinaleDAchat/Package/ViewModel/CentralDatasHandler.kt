package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.ViewModel

import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Stable
class CentralDatasHandler(
    val comptAppState: ComptAppState,
    val transactionCommercialState: TransactionCommercialState,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    val loadingProgress: Float? by derivedStateOf {
        comptAppState.loadingProgress.value + transactionCommercialState.loadingProgress.value
    }

    val ouvertC3_TransactionCommercial: C3_TransactionCommercial? by derivedStateOf {
        getOuvertData()
    }

    fun getOuvertData(): C3_TransactionCommercial? {
        return transactionCommercialState.datas.value.find {
            it.clientAcheteurID == comptAppState.activeClientPourCeCompt
                    && it.etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        }
    }
}
