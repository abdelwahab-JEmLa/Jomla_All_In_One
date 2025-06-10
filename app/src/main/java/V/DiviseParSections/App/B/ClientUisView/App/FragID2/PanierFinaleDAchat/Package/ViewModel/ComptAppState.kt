package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.ViewModel

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto._1_5_Vendeur._1_5_Vendeur
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
class ComptAppState(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<_1_5_Vendeur>>(emptyList())
    val datas: State<List<_1_5_Vendeur>> = _datas

    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: State<Float> = _loadingProgress

    val activeComptPourCeTelephone: _1_5_Vendeur? by derivedStateOf {
        getActiveComptPourCeTelephone(_datas.value)
    }

    val activeClientPourCeCompt by derivedStateOf {
        activeComptPourCeTelephone?.idClientOuvertPoutCeCompt ?: 0
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
                updateDatas(list)
            }
        }
    }

    fun updateDatas(newDatas: List<_1_5_Vendeur>) {
        _datas.value = newDatas
    }

    fun getActiveComptPourCeTelephone(datas: List<_1_5_Vendeur>): _1_5_Vendeur? {
        return datas.find { it.vid == 1L }
    }
}
