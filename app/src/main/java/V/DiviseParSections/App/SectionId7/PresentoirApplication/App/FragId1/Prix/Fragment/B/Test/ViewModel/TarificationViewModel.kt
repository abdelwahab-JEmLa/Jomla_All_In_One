package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.B.NoSQL.ConvertiseurNoSqlToSqlRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel.DataBase.A.SQL.Models.B_ClientInfos as SqlClientInfos

data class UiState(
    val outputModel: ProduitNoSqlDataBase = ProduitNoSqlDataBase(emptyList()),
    val isLoading: Boolean = false,
    val error: String? = null
)
class TarificationViewModel(
    private val convertiseurNoSqlToSqlRepository: ConvertiseurNoSqlToSqlRepository,
) : ViewModel() {
    private val TAG = "TarificationViewModel"
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    init {
        Log.d(TAG, "Initializing TarificationViewModel")
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            Log.d(TAG, "Starting to collect from noSqlDataFlow")

            convertiseurNoSqlToSqlRepository.noSqlDataFlow.collectLatest { noSqlData ->
                val collectTime = System.currentTimeMillis()
                Log.d(TAG, "Received new NoSQL data with ${noSqlData.produits.size} products")

                // Process data here if needed
                val processStartTime = System.currentTimeMillis()

                // Update UI state with new data
                _uiState.value = _uiState.value.copy(
                    outputModel = noSqlData,
                    isLoading = false
                )

                val updateCompleteTime = System.currentTimeMillis()
                Log.d(TAG, "UI state updated in ${updateCompleteTime - processStartTime}ms")
                Log.d(TAG, "Total flow collection handling time: ${updateCompleteTime - collectTime}ms")
            }
        }
    }

    fun getSqlProduit(id: Long): A_ProduitInfos? {
        return convertiseurNoSqlToSqlRepository.getProduitInfos(id)
    }

    fun getSqlClient(id: Long): SqlClientInfos? {
        return convertiseurNoSqlToSqlRepository.getClientInfos(id)
    }

    fun getSqlTypeTarification(id: Long): C_TypeTarificationInfos? {
        return convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(id)
    }

    fun getSqlTarifications(idProduit: Long, idClient: Long, idTypeTarification: Long): List<D_TarificationInfos> {
        return convertiseurNoSqlToSqlRepository.getTarificationInfos(idProduit, idClient, idTypeTarification)
    }

    fun refreshData() {
        Log.d(TAG, "Manual data refresh requested")
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                Log.d(TAG, "Manual data refresh completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error during manual data refresh", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to refresh data: ${e.message}"
                )
            }
        }
    }
}
