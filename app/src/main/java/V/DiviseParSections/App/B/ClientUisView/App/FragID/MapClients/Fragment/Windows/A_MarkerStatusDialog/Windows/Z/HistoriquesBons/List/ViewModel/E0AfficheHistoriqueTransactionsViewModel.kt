package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SecID5FragID2UiState(
    val activeCompt: Z_AppCompt? = Z_AppCompt(),
    val B_ClientInfosProtoJuin3List: List<HClientInfos> = emptyList(),
    val mainLoadingProgress: Float = 0f,
)

class E0AfficheHistoriqueTransactionsViewModel(
    aCentral: ACentralFacade,

    val a_CentralDatasHandlerProtoJuin9: AGetter,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,

    val r_0_0_HeadOfRepositorys_SQL_Repository: GroupeRepositorysProtoAvJuin3,
) : ViewModel() {
    val getter = aCentral.getter
    val setter =aCentral.setter
    val audioRecorderAndPlayHandler =aCentral.modulesCentral.audioRecorderAndPlayHandler

    val gBonVentRepository = getter.id8BonVentRepository

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

    fun notifyDataChanged() {
        viewModelScope.launch {
        }
    }

    fun openTransaction(data: M8BonVent): Unit {
        updateActiveComptIdClientOuvertPoutCeCompt(data.parentHClientOldID)
        setter.ouvreExistedDataEtNavigatePanie(data.keyID)
    }

    fun updateActiveComptIdClientOuvertPoutCeCompt(data: Long) {
     /*   val currentActiveCompt = _uiState.value.activeCompt ?: return
        _uiState.value = _uiState.value.copy(
            activeCompt = currentActiveCompt.copy(onVentFClientAncienId = data)
        )

        a_MasterRepositorysGrpProtoJuin3.e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model
            .repository_1_5_Vendeur
            .updateUnSeulData(currentActiveCompt.copy(onVentFClientAncienId = data))     */
    }
}
