package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Module.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorys
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update.addOrUpdateData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val d_EtateMessageVocaleList: List<D_EtateMessageVocale> = emptyList(),
    val mainLoadingProgress: Float = 0f
)

class ViewModelMessageur(
    private val masterRepositorys: A_MasterRepositorys,
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        d_EtateMessageVocaleList = model.d_EtateMessageVocaleRepository?.modelListFlow
                            ?: emptyList(),
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }

    fun addOrUpdateData(data: D_EtateMessageVocale): Unit { masterRepositorys.d_EtateMessageVocaleRepository.addOrUpdateData(data) }
}
