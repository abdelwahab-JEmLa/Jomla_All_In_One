package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.ACentral
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GrossistAchatSec12FragID1_ViewModel(
    aCentral: ACentral,
) : ViewModel() {
    val getter = aCentral.getter
   val fVentCouleurOperationRepository =getter.fVentCouleurOperationRepository
    data class UiState(
        val B_ClientInfosProtoJuin3List: List<HClientInfos> = emptyList(),
        val mainLoadingProgress: Float = 0f
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
