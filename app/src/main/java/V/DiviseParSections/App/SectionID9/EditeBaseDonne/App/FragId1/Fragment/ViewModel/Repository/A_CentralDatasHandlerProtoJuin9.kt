package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import android.util.Log
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
    val b3CategoriesCompoRepository: B3CategoriesCompoRepository,
    val clientsState: B_ClientsState,
    val comptAppState: D_ComptAppState,
    val transactionCommercialState: C_TransactionCommercialState,
    val autreStates: Z_AutreStates,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    val nombreClientsOuLeurDernierEtateCible: Int by derivedStateOf {
        clientsState.datasValue.count { client ->
            val lastTransaction = transactionCommercialState.getClientLastTransaction(client.id)
            lastTransaction?.etateActuellementEst in listOf(
                C3_TransactionCommercial.EtateActuellementEst.Cible,
            )
        }
    }
    val clientOuSonMarqueMapEstOuvert by derivedStateOf {
        clientsState.findClientById(
            comptAppState.idClientOuSonMarqueMapEstOuvert
        ) .also {  transaction ->
            Log.d("ouvertTransactionCommercial", "comptAppState.idClientOuSonMarqueMapEstOuvert" +
                    " ${comptAppState.idClientOuSonMarqueMapEstOuvert}")
        }
    }

    val ouvertTransactionCommercial: C3_TransactionCommercial? by derivedStateOf {
        clientOuSonMarqueMapEstOuvert?.let {
            transactionCommercialState.getClientLastTransactionParEtate(
                it.id,
                C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            ).also { transaction ->
                Log.d("ouvertTransactionCommercial", "Transaction ID: ${transaction?.vid}")
            }
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
