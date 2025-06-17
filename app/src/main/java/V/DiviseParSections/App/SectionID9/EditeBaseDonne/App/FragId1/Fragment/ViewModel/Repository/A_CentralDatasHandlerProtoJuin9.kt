package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository.D_AchatOperationComposeRepositoryPJ17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_AppCompt.Repository.Z_AppComptComposeRepositoryProtoJuin17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A2_Passive.B_ClientsState
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A2_Passive.C_TransactionCommercialState
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class A_CentralDatasHandlerProtoJuin9(
    private val context: Context,
    val databaseInitializationManager: Z_DatabaseInitializationManager,
    val comptAppState: D_ComptAppState,
    val appComptComposeRepositoryProtoJuin17: Z_AppComptComposeRepositoryProtoJuin17,

    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val b3CategoriesCompoRepository: B3CategoriesCompoRepository,
    val clientsState: B_ClientsState,
    val transactionCommercialState: C_TransactionCommercialState,

    val d_AchatOperationComposeRepositoryPJ17: D_AchatOperationComposeRepositoryPJ17,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    val loadingProgress: Float? by derivedStateOf {
        appComptComposeRepositoryProtoJuin17.currentAppCompt?.mainInitDataBaseProgressEtate
    }

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
        ).also { transaction ->
            Log.d(
                "ouvertTransactionCommercial", "comptAppState.idClientOuSonMarqueMapEstOuvert" +
                        " ${comptAppState.idClientOuSonMarqueMapEstOuvert}"
            )
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
            try {
                databaseInitializationManager.initializeAllRepositories(context)
            } catch (e: Exception) {
                databaseInitializationManager.updateMainInitDataBaseProgressEtate(1.0f)
            }
        }
    }
}
