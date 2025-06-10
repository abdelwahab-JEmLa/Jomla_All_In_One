package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto._1_5_Vendeur._1_5_Vendeur
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class D_ComptAppState(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<_1_5_Vendeur>>(emptyList())
    val datas: State<List<_1_5_Vendeur>> = _datas
    val datasValue by derivedStateOf { _datas.value }

    private val _activeCompt = mutableStateOf<_1_5_Vendeur?>(_1_5_Vendeur())
    val activeCompt by derivedStateOf { getActiveComptPourCeTelephone() }

    val activeClientPourCeCompt by derivedStateOf { activeCompt?.idClientOuvertPoutCeCompt ?: 0 }

    val size: Int by derivedStateOf { _datas.value.size }
    val isEmpty: Boolean by derivedStateOf { _datas.value.isEmpty() }

    init {
        composScope.launch {
            snapshotFlow {
                a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
                    .repositorys_Model
                    .repository_1_5_Vendeur
                    .modelDatasSnapList
                    .toList()
            }.collect { list ->
                addOrUpdateDatas(list)
            }
        }
    }

    fun updateActiveCompt(newActiveCompt: _1_5_Vendeur?) {
        _activeCompt.value = newActiveCompt

        _activeCompt.value?.let {
            a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model
                .repository_1_5_Vendeur
                .updateUnSeulData(it)
        }
    }

    fun updateActiveComptIdClientOuvertPoutCeCompt(idClientOuvertPoutCeCompt: Long) {
        val updatedCompt = activeCompt?.copy(idClientOuvertPoutCeCompt = idClientOuvertPoutCeCompt)
        addOrUpdateData(updatedCompt)
    }

    fun addOrUpdateData(data: _1_5_Vendeur?) {
        data?.let { newData ->
            _datas.value = _datas.value.map {
                if (it.vid == newData.vid) newData else it
            }.let { list ->
                if (list.none { it.vid == newData.vid }) list + newData else list
            }
            a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model
                .repository_1_5_Vendeur
                .updateUnSeulData(newData)
        }
    }

    fun addOrUpdateDatas(datas: List<_1_5_Vendeur>) { _datas.value = datas }

    fun getActiveComptPourCeTelephone(): _1_5_Vendeur? { return datasValue.find { it.vid == 1L } }
}
