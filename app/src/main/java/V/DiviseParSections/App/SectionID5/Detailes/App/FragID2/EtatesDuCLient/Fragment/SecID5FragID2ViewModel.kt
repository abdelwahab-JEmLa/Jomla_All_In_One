package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = ""

data class SecID5FragID2UiState(
    val l_1_4_PeriodeVent: List<_1_4_PeriodeVent>,
    val l_1_3_TransactionCommercial: List<_1_3_TransactionCommercial>,

    val transactionsDateToList_1_3_TransactionCommercial:
    List<Pair<_1_4_PeriodeVent, List<_1_3_TransactionCommercial>>> =
        emptyList(),
)


open class SecID5FragID2ViewModel(
    val r_0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecID5FragID2UiState())
    val uiState: StateFlow<SecID5FragID2UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            addTestDataToFireBaseIfEmpty()

            loadCollectUiState()
        }
    }

    private fun addTestDataToFireBaseIfEmpty() {
        viewModelScope.launch(Dispatchers.IO) {


        }
    }

    private fun loadCollectUiState() {
        viewModelScope.launch(Dispatchers.IO) {


        }
    }


    fun notifyDataChanged() {
        // Launch a coroutine to reload the data
        viewModelScope.launch {
            loadCollectUiState()
        }
    }
}
