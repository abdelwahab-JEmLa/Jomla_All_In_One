package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.AvantJuin17.Proto

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.F_OperationAchat.Repository.C.Update.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.F_OperationAchat.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.F_OperationAchat.Repository.C.Update.deleteAddMultiDatas
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.F_OperationAchat.Repository.Z_AppComptRepository
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class Z_AppComptComposeRepository(
    appComptRepository: Z_AppComptRepository
) {
    private val TAG = "Z_AppComptComposeRepository"
    private val parentRepo = appComptRepository
    private val dao = appComptRepository.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<Z_AppCompt>>(emptyList())
    val datasState: State<List<Z_AppCompt>> = _datas
    val datasValue by derivedStateOf { _datas.value }
    val tigerDataRecompose by derivedStateOf { _datas.value.map { it.dernierTimeTampsSynchronisationAvecFireBase } }

    private val currentAppCompt by derivedStateOf { datasValue.find { it.bsonObjectId == "b1" } }
    val mainInitDataBaseProgressEtate_appComptActuelle by derivedStateOf { currentAppCompt?.mainInitDataBaseProgressEtate ?: 0f }

    init {
        composScope.launch {
            dao.getAllFlow().collect { masterModel ->
                _datas.value = masterModel
            }
        }
    }

    suspend fun updateMainInitDataBaseProgressEtate(loadingProgress: Float) {
        // Remove withContext(Dispatchers.Main.immediate) to avoid suspension point in critical section
        currentAppCompt?.let { appCompt ->
            val updatedAppCompt = appCompt.copy(
                mainInitDataBaseProgressEtate = loadingProgress
            )

            // Update local state first
            val updatedList = _datas.value.map {
                if (it.bsonObjectId == updatedAppCompt.bsonObjectId)
                    updatedAppCompt
                else it
            }
            _datas.value = updatedList

            // Then update repository in background
            composScope.launch {
                parentRepo.addOrUpdateData(updatedAppCompt)
            }
        }
    }
    fun addOrUpdateData(data: Z_AppCompt) {
        data.let { dataSansProper ->
            val newData = dataSansProper.withDernierTimeTampsSynchronisationAvecFireBase()

            val updatedList = _datas.value.map {
                if (it.bsonObjectId == newData.bsonObjectId)
                    newData
                else it
            }
            Z_AppCompt.logCategory(newData, TAG)
            _datas.value = updatedList

            composScope.launch {
                updateSonRepositoryProtoJuin3(newData)
            }
        }
    }

    private fun updateSonRepositoryProtoJuin3(newData: Z_AppCompt) {
        parentRepo.addOrUpdateData(newData)
    }

    fun addOrUpdateDatas(datas: List<Z_AppCompt>) {
        val processedDatas = datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }

        val currentList = _datas.value.toMutableList()

        processedDatas.forEach { newData ->
            val existingIndex = currentList.indexOfFirst { it.bsonObjectId == newData.bsonObjectId }

            if (existingIndex >= 0) {
                currentList[existingIndex] = newData
            } else {
                currentList.add(newData)
            }
        }

        _datas.value = currentList

        composScope.launch {
            updateDatasDonSonRepositoryProtoJuin3(processedDatas)
        }
    }

    private fun updateDatasDonSonRepositoryProtoJuin3(newDatas: List<Z_AppCompt>) {
        parentRepo.addOrUpdateDatas(newDatas)
    }

    fun deleteAddMultiDatas(newDatas: List<Z_AppCompt>) {
        parentRepo.deleteAddMultiDatas(newDatas)
    }
}
