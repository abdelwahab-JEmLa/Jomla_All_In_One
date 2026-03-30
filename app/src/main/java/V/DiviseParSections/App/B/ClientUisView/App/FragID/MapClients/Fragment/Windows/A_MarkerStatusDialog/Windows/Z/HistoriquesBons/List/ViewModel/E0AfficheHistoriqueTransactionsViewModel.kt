package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.M09AppCompt
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
    val activeCompt: M09AppCompt? =null,
    val B_ClientInfosProtoJuin3List: List<M2Client> = emptyList(),
    val mainLoadingProgress: Float = 0f,
)

class E0AfficheHistoriqueTransactionsViewModel(
    val aCentralFacade: ACentralFacade,

    val a_CentralDatasHandlerProtoJuin9: RepositorysMainGetter,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,

    val r_0_0_HeadOfRepositorys_SQL_Repository: GroupeRepositorysProtoAvJuin3,
) : ViewModel() {
    val getter = aCentralFacade.repositorysMainGetter
    val setter =aCentralFacade.repositorysMainSetter
    val audioRecorderAndPlayHandler =aCentralFacade.modulesCentral.audioRecorderAndPlayHandler

    val gBonVentRepository = getter.repo8BonVent

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
        updateActiveComptIdClientOuvertPoutCeCompt(data.parent_M2Client_OldLongID)
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
