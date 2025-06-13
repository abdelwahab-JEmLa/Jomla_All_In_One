package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class C_TransactionCommercialState(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<C3_TransactionCommercial>>(emptyList())
    val datasState: State<List<C3_TransactionCommercial>> = _datas
    val datasValue by derivedStateOf { _datas.value }


    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: State<Float> = _loadingProgress

    val size: Int by derivedStateOf { _datas.value.size }
    val isEmpty: Boolean by derivedStateOf { _datas.value.isEmpty() }

    init {
        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    updateLoadingProgress(model.progress)
                }
            }
        }

        composScope.launch {
            snapshotFlow {
                a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
                    .repositorys_Model
                    .c3TransactionCommercialRepository
                    .modelDatasSnapList
                    .toList()
            }.collect { list ->
                updateDatas(list)
            }
        }
    }
    fun getClientLastTransactionParEtate(
        clientId: Long, etateActuellementEst: C3_TransactionCommercial.EtateActuellementEst =
            C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    ): C3_TransactionCommercial? {
        return datasValue
            .filter {
                it.clientAcheteurID == clientId
                        && it.etateActuellementEst == etateActuellementEst
            }
            .maxByOrNull { it.timestamps }
    }

    fun getClientLastTransaction(clientId: Long): C3_TransactionCommercial? {
        return datasValue
            .filter {
                it.clientAcheteurID == clientId
            }
            .maxByOrNull { it.timestamps }
    }

    fun updateLoadingProgress(progress: Float) {
        _loadingProgress.floatValue = progress
    }

    fun updateDatas(newDatas: List<C3_TransactionCommercial>) {
        _datas.value = newDatas
    }
}
