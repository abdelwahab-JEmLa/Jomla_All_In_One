package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository

import Z_CodePartageEntreApps.DataBase.Juin17.Proto.D_AchatOperationRepository.Base.D_AchatOperationDataBaseProtoJuin17
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class E_AchatOperationComposeRepositoryProtoJuin17(
    private val ancienRepo: D_AchatOperationDataBaseProtoJuin17,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<D_AchatOperation>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }
    val tigerDataRecompose by derivedStateOf { _datas.value.map { it.dernierTimeTampsSynchronisationAvecFireBase } }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun addOrUpdateData(data: D_AchatOperation) {
        val dataAvecTigerUpdate = data.withDernierTimeTampsSynchronisationAvecFireBase()
        val existingIndex = datasValue.indexOfFirst { ancien ->
            D_AchatOperation.compareEntre(ancien = ancien, newData = dataAvecTigerUpdate)
        }
        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(
                    quantityAchete = this[existingIndex].quantityAchete + dataAvecTigerUpdate.quantityAchete,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            }
        } else {
            datasValue + dataAvecTigerUpdate
        }

        ancienRepo.addOrUpdatedAncienRepo(existingIndex, dataAvecTigerUpdate)
    }

}

