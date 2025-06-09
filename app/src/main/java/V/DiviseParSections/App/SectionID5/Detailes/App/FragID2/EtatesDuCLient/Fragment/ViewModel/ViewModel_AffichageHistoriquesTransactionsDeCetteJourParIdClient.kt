package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Preview.addTestDataToFireBaseIfEmpty
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
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
import java.util.Locale

data class SecID5FragID2UiState(
    val B_ClientInfosProtoJuin3List: List<B_ClientInfosProtoJuin3> = emptyList(),
    val mainLoadingProgress: Float = 0f,

    val sl_1_4_PeriodeVent: SnapshotStateList<_1_4_PeriodeVent> = SnapshotStateList(),
    val sl_C_3_BonAchate: SnapshotStateList<C3_TransactionCommercial> = SnapshotStateList(),
    val transactionsDateToList_C_3_BonAchate: List<Pair<_1_4_PeriodeVent, List<C3_TransactionCommercial>>> = emptyList(),
)

class ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,

    val r_0_0_HeadOfRepositorys_SQL_Repository: GroupeRepositorysProtoAvJuin3,
    private val navigationHandler: FragmentNavigationHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecID5FragID2UiState())
    val uiState: StateFlow<SecID5FragID2UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        B_ClientInfosProtoJuin3List = model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList(),
                        // FIXED: use lowercase 'b' to match the property name in MasterRepositorysModel
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }

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

    fun deleteVoiceRecordingFromStorage(vocaleKeyID: String, onComplete: (Boolean) -> Unit) {
        if (vocaleKeyID.isBlank()) {
            onComplete(true)
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val voiceRef = storageRef.child("1_messagesVocales/$vocaleKeyID")

        voiceRef.metadata
            .addOnSuccessListener {
                voiceRef.delete()
                    .addOnSuccessListener {
                        onComplete(true)
                    }
                    .addOnFailureListener {
                        onComplete(false)
                    }
            }
            .addOnFailureListener { exception ->
                if (exception.message?.contains("Object does not exist", ignoreCase = true) == true) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }

    private fun loadCollectSnapshotStateList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_4_PeriodeVent.ensureDataIsInitialized()
                r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.c3TransactionCommercialRepository.ensureDataIsInitialized()

                if (r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList.isEmpty() ||
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.c3TransactionCommercialRepository.modelDatasSnapList.isEmpty()
                ) {
                    addTestDataToFireBaseIfEmpty(viewModelScope, r_0_0_HeadOfRepositorys_SQL_Repository)
                    delay(1000)
                }

                withContext(Dispatchers.Main) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            sl_1_4_PeriodeVent = r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList,
                            sl_C_3_BonAchate = r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.c3TransactionCommercialRepository.modelDatasSnapList,
                        )
                    }
                }

                loadCollecttransactionsDateToList_1_3_TransactionCommercial()

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
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.c3TransactionCommercialRepository.let { repo ->
                        snapshotFlow { repo.modelDatasSnapList.toList() }.collect {
                            withContext(Dispatchers.Main) {
                                _uiState.update { currentState ->
                                    currentState.copy(sl_C_3_BonAchate = repo.modelDatasSnapList)
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
                val transactions = _uiState.value.sl_C_3_BonAchate.toList()

                val sortedTransactions = transactions.sortedByDescending { transaction ->
                    transaction.timestamps
                }

                val sortedPeriods = periods.sortedByDescending { period ->
                    try {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(period.startDateInString)?.time ?: 0L
                    } catch (e: Exception) {
                        0L
                    }
                }

                val groupedTransactions = sortedPeriods.map { period ->
                    val periodTransactions = sortedTransactions.filter { transaction ->
                        transaction.parentVID_1_4_PeriodeVent == period.vid
                    }
                    Pair(period, periodTransactions)
                }

                withContext(Dispatchers.Main) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            transactionsDateToList_C_3_BonAchate = groupedTransactions
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun notifyDataChanged() {
        viewModelScope.launch {
            loadCollectSnapshotStateList()
        }
    }
}
