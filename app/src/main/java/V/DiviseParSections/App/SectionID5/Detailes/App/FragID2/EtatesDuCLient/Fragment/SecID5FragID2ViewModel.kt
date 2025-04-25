package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "SecID5FragID2ViewModel"

data class SecID5FragID2UiState(
    val sl_1_4_PeriodeVent: SnapshotStateList<_1_4_PeriodeVent> = SnapshotStateList(),
    val sl_1_3_TransactionCommercial: SnapshotStateList<_1_3_TransactionCommercial> = SnapshotStateList(),
    val transactionsDateToList_1_3_TransactionCommercial:
    List<Pair<_1_4_PeriodeVent, List<_1_3_TransactionCommercial>>> = emptyList(),
)

class SecID5FragID2ViewModel(
    private val r_0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecID5FragID2UiState())
    val uiState: StateFlow<SecID5FragID2UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCollectSnapshotStateList()
        }
    }

    private fun loadCollectSnapshotStateList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ensure repositories are initialized
                r_0_0_HeadOfRepositorys_Repository.repositorys_Model.repository_1_4_PeriodeVent.ensureDataIsInitialized()
                r_0_0_HeadOfRepositorys_Repository.repositorys_Model.repository_1_3_TransactionCommercial.ensureDataIsInitialized()

                // Get the snapshot state lists
                val periods = r_0_0_HeadOfRepositorys_Repository.repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList
                val transactions = r_0_0_HeadOfRepositorys_Repository.repositorys_Model.repository_1_3_TransactionCommercial.modelDatasSnapList

                withContext(Dispatchers.Main) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            sl_1_4_PeriodeVent = periods,
                            sl_1_3_TransactionCommercial = transactions,
                        )
                    }
                }

                // Load the grouped transactions after loading the lists
                loadCollecttransactionsDateToList_1_3_TransactionCommercial()

                // Add test data if the repositories are empty
                if (periods.isEmpty() || transactions.isEmpty()) {
                    addTestDataToFireBaseIfEmpty()
                }
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
            }
        }
    }

    private fun addTestDataToFireBaseIfEmpty() {     //<--
    //TODO(1): cree d  autre 6> _1_3_TransactionCommercial  du client id 2 avec diffrents 
    //etates et periodes
        viewModelScope.launch(Dispatchers.IO) {
            // Add test period if needed
            if (r_0_0_HeadOfRepositorys_Repository.repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList.isEmpty()) {
                // First test period (today)
                val testPeriod1 = _1_4_PeriodeVent(
                    vendeur_ParentVID = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L,
                    startDateInString = _1_4_PeriodeVent.getMainValeKey(),
                    heurDebutInString = "08:00",
                    endDateInString = "",
                    etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
                )

                // Second test period (yesterday)
                val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                )
                val testPeriod2 = _1_4_PeriodeVent(
                    vendeur_ParentVID = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L,
                    startDateInString = yesterday,
                    heurDebutInString = "09:00",
                    endDateInString = "",
                    etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.CONFIRME
                )

                // Third test period (day before yesterday)
                val dayBeforeYesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)
                )
                val testPeriod3 = _1_4_PeriodeVent(
                    vendeur_ParentVID = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L,
                    startDateInString = dayBeforeYesterday,
                    heurDebutInString = "10:00",
                    endDateInString = "",
                    etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.NA_PAS_COMMANDE
                )

                // Fourth test period (tomorrow)
                val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
                )
                val testPeriod4 = _1_4_PeriodeVent(
                    vendeur_ParentVID = r_0_0_HeadOfRepositorys_Repository.currentVendeur?.vid ?: 0L,
                    startDateInString = tomorrow,
                    heurDebutInString = "11:00",
                    endDateInString = "",
                    etateActuellementEst = _1_4_PeriodeVent.EtateActuellementEst.ENTRE_MAIS_PAS_CONFIRME
                )

                // Add first period and create transaction for it
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod1) { periodVid ->
                    // Add test transaction using the period ID
                    val testTransaction = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 1L,
                        heurDebutInString = "09:00",
                        heurFinInString = "10:30",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                        fireBaseKeyID = "$periodVid->(1->(ON_MODE_COMMEND_ACTUELLEMENT))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(testTransaction)
                }

                // Add remaining periods
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod2) { periodVid ->
                    val transaction = _1_3_TransactionCommercial(
                        parentVID_1_4_PeriodeVent = periodVid,
                        clientAcheteurID = 2L,
                        heurDebutInString = "10:00",
                        heurFinInString = "11:45",
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME,
                        fireBaseKeyID = "$periodVid->(2->(A_COMMANDE_CONFIRME))",
                        vid = 0L
                    )
                    r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_3_TransactionCommercial(transaction)
                }

                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod3)
                r_0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID_1_4_PeriodeVent(testPeriod4)
            }
        }
    }
    private fun loadCollecttransactionsDateToList_1_3_TransactionCommercial() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val periods = _uiState.value.sl_1_4_PeriodeVent
                val transactions = _uiState.value.sl_1_3_TransactionCommercial

                // Group transactions by period
                val groupedTransactions = periods.map { period ->
                    val periodTransactions = transactions.filter { transaction ->
                        transaction.parentVID_1_4_PeriodeVent == period.vid
                    }
                    Pair(period, periodTransactions)
                }

                withContext(Dispatchers.Main) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            transactionsDateToList_1_3_TransactionCommercial = groupedTransactions
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun notifyDataChanged() {
        // Launch a coroutine to reload the data
        viewModelScope.launch {
            loadCollectSnapshotStateList()
        }
    }
}
