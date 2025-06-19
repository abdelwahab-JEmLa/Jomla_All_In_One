package Views.P1.Ui.ArticlesGrid.A.List.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.AvantJuin3._1_5_Vendeur.Proto._1_5_Vendeur
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

    private val _datas = mutableStateOf<List<_1_5_Vendeur>>(emptyList())
    val datas: State<List<_1_5_Vendeur>> = _datas
    val datasValue by derivedStateOf { _datas.value }

    val activeCompt by derivedStateOf { getActiveComptPourCeTelephone() }
    val idClientOuSonMarqueMapEstOuvert by derivedStateOf { activeCompt?.idClientOuSonMarqueMapEstOuvert ?: 0 }

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
    fun getActiveComptPourCeTelephone(): _1_5_Vendeur? { return datasValue.find { it.vid == 1L } }

    fun updateActiveComptIdClientOuSonMarqueMapEstOuvert(idClientOuSonMarqueMapEstOuvert: Long) {
        val updatedCompt = activeCompt?.copy(idClientOuSonMarqueMapEstOuvert = idClientOuSonMarqueMapEstOuvert)
        addOrUpdateData(updatedCompt)
    }

    fun addOrUpdateData(data: _1_5_Vendeur?) {
        data?.let { dataSansProper ->
            val newData= dataSansProper.withProperKeyFireBaseAndTimeTamp()
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

    private fun update_a_MasterRepositorysGrpProtoJuin3(newData: _1_5_Vendeur) {
        a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin
            .repositorys_Model
            .repository_1_5_Vendeur
            .updateUnSeulData(newData)
    }

    fun addOrUpdateDatasComposClass(datas: List<_1_5_Vendeur>) { _datas.value = datas }
}
