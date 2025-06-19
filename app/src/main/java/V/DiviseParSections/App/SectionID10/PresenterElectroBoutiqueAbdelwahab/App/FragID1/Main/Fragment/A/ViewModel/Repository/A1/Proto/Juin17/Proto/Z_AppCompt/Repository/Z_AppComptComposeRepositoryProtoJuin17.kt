package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_AppCompt.Repository

import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.A.Model.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Z_AppComptRepositoryProtoJuin17
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class Z_AppComptComposeRepositoryProtoJuin17(
    private val ancienRepo: Z_AppComptRepositoryProtoJuin17,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<Z_AppCompt>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

     val currentAppCompt by derivedStateOf { datasValue.find { it.bsonObjectId == "b1" } }

    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun addOrUpdateData(data: Z_AppCompt) {
        val dataAvecTigerUpdate = data.withDernierTimeTampsSynchronisationAvecFireBase()
        val existingIndex = datasValue.indexOfFirst { ancien ->
            Z_AppCompt.compareEntre(ancien = ancien, newData = dataAvecTigerUpdate)
        }
        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            }
        } else {
            datasValue + dataAvecTigerUpdate
        }

        ancienRepo.addOrUpdatedDataBase(existingIndex, dataAvecTigerUpdate)
    }

}

