package Z_CodePartageEntreApps.Repository.Main.Proto

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
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
class Z_ComptAppStateCompoRepositoryProtoAvanJuin17(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<Z_AppCompt>>(emptyList())
    val datas: State<List<Z_AppCompt>> = _datas
    val datasValue by derivedStateOf { _datas.value }

    val activeCompt by derivedStateOf { getActiveComptPourCeTelephone() }
    val idClientOuSonMarqueMapEstOuvert by derivedStateOf {
        activeCompt?.onVentFClientAncienId ?: 0
    }

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
                addOrUpdateDatasComposClass(list)
            }
        }
    }
    fun getActiveComptPourCeTelephone(): Z_AppCompt? { return datasValue.find { it.vid == 1L } }


    fun updateActiveComptIdClientOuSonMarqueMapEstOuvert(idClientOuSonMarqueMapEstOuvert: Long) {
        val updatedCompt = activeCompt?.copy(onVentFClientAncienId = idClientOuSonMarqueMapEstOuvert)
        addOrUpdateData(updatedCompt)
    }

    fun addOrUpdateData(data: Z_AppCompt?) {
        data?.let { dataSansProper ->
            val newData= dataSansProper
            _datas.value = _datas.value.map {
                if (it.vid == newData.vid)
                    newData
                else it
            }.let { list ->
                if (list.none { it.vid == newData.vid }) list + newData else list
            }
            update_a_MasterRepositorysGrpProtoJuin3(newData)
        }
    }

    private fun update_a_MasterRepositorysGrpProtoJuin3(newData: Z_AppCompt) {
        a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
            .repositorys_Model
            .repository_1_5_Vendeur
            .updateUnSeulData(newData)
    }

    fun addOrUpdateDatasComposClass(datas: List<Z_AppCompt>) { _datas.value = datas }
}
