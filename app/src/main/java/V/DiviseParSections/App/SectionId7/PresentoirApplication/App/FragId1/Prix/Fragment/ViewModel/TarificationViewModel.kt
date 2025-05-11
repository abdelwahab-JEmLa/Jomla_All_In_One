package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.InputEtInfosSqlModels
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.OutputNoSqlModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Repository.Output.OutputNoSqlModelRepositoryImp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Passive.createTimestamp
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TarificationViewModel(
     val testCallbacks: TestCallbacks? = null
) : ViewModel() {

    interface TestCallbacks {
        // Updated to accept a generic parameter for the result
        fun <T> onOperationSuccess(result: T)
    }

    private val inputSqlGroupeRepositorys = InputEtInfosSqlGroupeRepositorysImp(testCallbacks)

    private val outputNoSqlModelRepository =
        OutputNoSqlModelRepositoryImp(inputSqlGroupeRepositorys)

    private val _OutputNoSqlFlow = MutableStateFlow(OutputNoSqlModel(emptyList()))
    val outputNoSqlFlow: StateFlow<OutputNoSqlModel> = _OutputNoSqlFlow.asStateFlow()

    private val inputSqlClientRepo = inputSqlGroupeRepositorys.ClientDataBase_Repository()
    private val typeTarificationInputSqlRepo = inputSqlGroupeRepositorys
        .TypeTarificationInfosRepository()
    private val inputSqlProduitInfosRepository = inputSqlGroupeRepositorys
        .ProduitInfosRepository()

    init {
        observeTarificationData()
    }

    private fun observeTarificationData() {
        viewModelScope.launch {
            outputNoSqlModelRepository.dataFlow.collectLatest { data ->
                _OutputNoSqlFlow.value = data
            }
        }
    }

    fun addNewTestDataTarificationEtClient() {
        val newTarification = InputEtInfosSqlModels.Tarification(
            vidTimestamp = createTimestamp(day = 10, hour = 16, minute = 30),
            idProduit = 1L,
            idClient = 1L,
            idTypeTarification = 2L,
            prixCurrency = 9.99
        )

        inputSqlGroupeRepositorys.TarificationRepository().add(newTarification) { addedTarification ->
            val client = inputSqlClientRepo.modelList.find { clientToUpdate ->
                clientToUpdate.id == addedTarification.idClient
            }?.copy(
                idActiveTypeTarificationDataBase = addedTarification.idTypeTarification
            )
            if (client != null) {
                inputSqlClientRepo.update(client)
            }
        }
    }

    fun getSqlProduitInfos(id: Long): InputEtInfosSqlModels.ProduitInfos? {
        return inputSqlProduitInfosRepository.modelList.find { it.id == id }
    }

    fun getSqlClient(idClient: Long): InputEtInfosSqlModels.ClientDataBase? {
        return inputSqlClientRepo.modelList.find { it.id == idClient }
    }

    fun getSqlTypeTarification(id: Long): InputEtInfosSqlModels.TypeTarificationDataBase? {
        return typeTarificationInputSqlRepo.modelList.find { it.id == id }
    }
    fun notifySuccess(result: Any) {
        Log.d("TarificationViewModel", "Notifying success with result: $result")
        testCallbacks?.onOperationSuccess(result)
    }
}
