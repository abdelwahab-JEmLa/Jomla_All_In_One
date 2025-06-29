package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ACentral
import V.DiviseParSections.App.Shared.Repository.ACentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.Z_App.Base._1_5_Vendeur
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SecID5FragID2UiState(
    val activeCompt: _1_5_Vendeur? = _1_5_Vendeur(),
    val B_ClientInfosProtoJuin3List: List<B_ClientInfosProtoJuin3> = emptyList(),
    val mainLoadingProgress: Float = 0f,

)

class E0AfficheHistoriqueTransactionsViewModel(
    aCentral: ACentral,
    val a_CentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,

    val r_0_0_HeadOfRepositorys_SQL_Repository: GroupeRepositorysProtoAvJuin3,
) : ViewModel() {
    val getter = aCentral.getter
    val setter =aCentral.setter
    val gBonVentRepository = getter.gBonVentRepository

    private val _uiState = MutableStateFlow(SecID5FragID2UiState())
    val uiState: StateFlow<SecID5FragID2UiState> = _uiState.asStateFlow()

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
                if (exception.message?.contains(
                        "Object does not exist",
                        ignoreCase = true
                    ) == true
                ) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }
        /*
    private fun loadCollectSnapshotStateList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repositoryMVentPeriode.ensureDataIsInitialized()
                r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.c3TransactionCommercialRepository.ensureDataIsInitialized()

                if (r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repositoryMVentPeriode.modelDatasSnapList.isEmpty() ||
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.c3TransactionCommercialRepository.modelDatasSnapList.isEmpty()
                ) {
                    addTestDataToFireBaseIfEmpty(
                        viewModelScope,
                        r_0_0_HeadOfRepositorys_SQL_Repository
                    )
                    delay(1000)
                }

                withContext(Dispatchers.Main) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            sl_1_4_PeriodeVent = r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repositoryMVentPeriode.modelDatasSnapList,
                            sl_C_3_BonAchate = r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.c3TransactionCommercialRepository.modelDatasSnapList,
                        )
                    }
                }

                loadCollecttransactionsDateToList_1_3_TransactionCommercial()

                launch {
                    r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.repositoryMVentPeriode.let { repo ->
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
                        transaction.parentPeriodeVentOldID == period.vid
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
    }     */

    fun notifyDataChanged() {
        viewModelScope.launch {
        }
    }

    fun openTransaction(data: GBonVent): Unit {
        updateActiveComptIdClientOuvertPoutCeCompt(data.parentHClientOldID)
        setter.ouvreExistedDataEtNavigeAuPanie(data.keyID)
    }

    fun updateActiveComptIdClientOuvertPoutCeCompt(data: Long) {
        val currentActiveCompt = _uiState.value.activeCompt ?: return
        _uiState.value = _uiState.value.copy(
            activeCompt = currentActiveCompt.copy(idClientOuSonMarqueMapEstOuvert = data)
        )

        a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model
            .repository_1_5_Vendeur
            .updateUnSeulData(currentActiveCompt.copy(idClientOuSonMarqueMapEstOuvert = data))
    }
}
