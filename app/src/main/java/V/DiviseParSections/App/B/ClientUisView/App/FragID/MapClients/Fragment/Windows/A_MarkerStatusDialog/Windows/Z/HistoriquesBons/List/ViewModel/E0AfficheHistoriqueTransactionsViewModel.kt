package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import android.annotation.SuppressLint
import android.content.Context
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

@SuppressLint("StaticFieldLeak")
class E0AfficheHistoriqueTransactionsViewModel(
    private val context: Context,
    val appDatabase: AppDatabase,
    val aCentralFacade: ACentralFacade,
    val a_CentralDatasHandlerProtoJuin9: RepositorysMainGetter,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val r_0_0_HeadOfRepositorys_SQL_Repository: GroupeRepositorysProtoAvJuin3,
    val repositorysMainSetter_NewProtoPatterns: RepositorysMainSetter_NewProtoPatterns = RepositorysMainSetter_NewProtoPatterns(
        appDatabase = appDatabase,
        context = context
    ),
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

    fun fireBase_batch_set_list_M01Produit(datas: List<M01Produit>) {
        repositorysMainSetter_NewProtoPatterns.fireBase_batch_set_list_M01Produit(datas)
    }

    // FIXED: Completed the empty function with proper implementation
    // This function notifies observers that data has changed and triggers a refresh
    fun notifyDataChanged() {
        viewModelScope.launch {
            // Refresh the UI state by updating the main loading progress
            _uiState.value = _uiState.value.copy(
                mainLoadingProgress = 1f
            )

            // Trigger data refresh in repositories if needed
            try {
                // Refresh BonVent repository
                gBonVentRepository.refresh_Datas()

                // Optionally refresh other related repositories
                getter.repo10OperationVentCouleur.refresh_Datas()
                getter.repo1ProduitInfos.refresh_Datas()

            } catch (e: Exception) {
                e.printStackTrace()
                // Log error or handle gracefully
            } finally {
                // Reset loading progress
                _uiState.value = _uiState.value.copy(
                    mainLoadingProgress = 0f
                )
            }
        }
    }
}
