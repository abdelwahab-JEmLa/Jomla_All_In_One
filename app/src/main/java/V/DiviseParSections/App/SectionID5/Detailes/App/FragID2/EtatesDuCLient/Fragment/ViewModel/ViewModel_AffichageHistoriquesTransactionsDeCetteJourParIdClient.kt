package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_4_PeriodeVent
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Preview.addTestDataToFireBaseIfEmpty
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient"

data class SecID5FragID2UiState(
    val sl_1_4_PeriodeVent: SnapshotStateList<_1_4_PeriodeVent> = SnapshotStateList(),
    val sl_1_3_TransactionCommercial: SnapshotStateList<_1_3_TransactionCommercial> = SnapshotStateList(),
    val sl_3_ClientsDataBase: SnapshotStateList<_3_ClientsDataBase> = SnapshotStateList(),

    val transactionsDateToList_1_3_TransactionCommercial:
    List<Pair<_1_4_PeriodeVent, List<_1_3_TransactionCommercial>>> = emptyList(),
)

class ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    val r_0_0_HeadOfRepositorys_SQL_Repository: _0_0_HeadSQLRepositorys,
    private val navigationHandler: FragmentNavigationHandler

) : ViewModel() {

    private val _uiState = MutableStateFlow(SecID5FragID2UiState())
    val uiState: StateFlow<SecID5FragID2UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCollectSnapshotStateList()
        }
    }

    fun navigateToCartScreen() {
        viewModelScope.launch(Dispatchers.Main) {
            navigationHandler.navigateToCartScreen()
        }
    }

    // Add this function to your ViewModel class
    fun deleteVoiceRecordingFromStorage(vocaleKeyID: String, onComplete: (Boolean) -> Unit) {
        // Check if the vocaleKeyID is valid before attempting to delete
        if (vocaleKeyID.isBlank()) {
            // If vocaleKeyID is empty or blank, consider deletion successful since there's nothing to delete
            Log.d(TAG, "No voice recording ID provided, skipping deletion")
            onComplete(true)
            return
        }

        // Attempting to delete the voice recording from Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val voiceRef = storageRef.child("voice_recordings/$vocaleKeyID")

        // First check if the file exists
        voiceRef.metadata
            .addOnSuccessListener { metadata ->
                // File exists, proceed with deletion
                voiceRef.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Voice recording $vocaleKeyID deleted successfully")
                        onComplete(true)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to delete existing voice recording: ${exception.message}")
                        onComplete(false)
                    }
            }
            .addOnFailureListener { exception ->
                // File doesn't exist, which is fine - we wanted to delete it anyway
                if (exception.message?.contains("Object does not exist", ignoreCase = true) == true) {
                    Log.w(TAG, "Voice recording $vocaleKeyID doesn't exist, considering deletion successful")
                    onComplete(true)
                } else {
                    // Some other error occurred when checking existence
                    Log.e(TAG, "Error checking voice recording existence: ${exception.message}")
                    onComplete(false)
                }
            }
    }
    private fun loadCollectSnapshotStateList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ensure repositories are initialized
                r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_4_PeriodeVent.ensureDataIsInitialized()
                r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_3_TransactionCommercial.ensureDataIsInitialized()
                r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_3_ClientsDataBase.ensureDataIsInitialized()

                // Add test data if the repositories are empty - check more reliably
                if (r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList.isEmpty() ||
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_3_TransactionCommercial.modelDatasSnapList.isEmpty()   ||
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_3_ClientsDataBase.modelDatasSnapList.isEmpty()

                ) {
                    addTestDataToFireBaseIfEmpty(viewModelScope, r_0_0_HeadOfRepositorys_SQL_Repository)
                    // Wait briefly for data to be saved
                    delay(1000)
                }

                // Update UI with current snapshot data
                withContext(Dispatchers.Main) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            sl_1_4_PeriodeVent = r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList,
                            sl_1_3_TransactionCommercial = r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model
                                .repository_1_3_TransactionCommercial.modelDatasSnapList,
                            sl_3_ClientsDataBase = r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model
                                .repository_3_ClientsDataBase.modelDatasSnapList
                        )
                    }
                }

                // Process the grouped data once immediately
                loadCollecttransactionsDateToList_1_3_TransactionCommercial()

                // Now set up continuous monitoring in separate coroutines
                launch {
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_4_PeriodeVent.let { repo ->
                        snapshotFlow { repo.modelDatasSnapList.toList() }.collect {
                            withContext(Dispatchers.Main) {
                                _uiState.update { currentState ->
                                    currentState.copy(sl_1_4_PeriodeVent = repo.modelDatasSnapList)
                                }
                            }
                            loadCollecttransactionsDateToList_1_3_TransactionCommercial()
                        }
                    }
                }

                launch {
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_3_TransactionCommercial.let { repo ->
                        snapshotFlow { repo.modelDatasSnapList.toList() }.collect {
                            withContext(Dispatchers.Main) {
                                _uiState.update { currentState ->
                                    currentState.copy(sl_1_3_TransactionCommercial = repo.modelDatasSnapList)
                                }
                            }
                            loadCollecttransactionsDateToList_1_3_TransactionCommercial()
                        }
                    }
                }

                launch {
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_3_ClientsDataBase.let { repo ->
                        snapshotFlow { repo.modelDatasSnapList.toList() }.collect {
                            withContext(Dispatchers.Main) {
                                _uiState.update { currentState ->
                                    currentState.copy(sl_3_ClientsDataBase = repo.modelDatasSnapList)
                                }
                            }
                            loadCollecttransactionsDateToList_1_3_TransactionCommercial()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadCollecttransactionsDateToList_1_3_TransactionCommercial() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val periods = _uiState.value.sl_1_4_PeriodeVent.toList()
                val transactions = _uiState.value.sl_1_3_TransactionCommercial.toList()

                // Sort periods by date in descending order (newest first)
                val sortedPeriods = periods.sortedByDescending { period ->
                    try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(period.startDateInString)
                    } catch (e: Exception) {
                        // Handle parsing errors by using a default old date
                        Date(0)
                    }
                }

                // Group transactions by period, maintaining the sorted order
                val groupedTransactions = sortedPeriods.map { period ->
                    val periodTransactions = transactions.filter { transaction ->
                        transaction.parentVID_1_4_PeriodeVent == period.vid
                    }.sortedByDescending { transaction ->
                        // For transactions in the same period, sort by transaction time if available
                        try {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).parse(transaction.heurDebutInString)
                        } catch (e: Exception) {
                            // Handle parsing errors
                            Date(0)
                        }
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
