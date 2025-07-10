package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GrossistAchatSec12FragID1_ViewModel(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    val getter = aCentralFacade.repositorysMainGetter
    val fVentCouleurOperationRepository = getter.repo10OperationVentCouleur

    data class UiState(
        val showMenu: Boolean = false,
        val showDialog: Boolean = false,
        val B_ClientInfosProtoJuin3List: List<M2Client> = emptyList(),
        val mainLoadingProgress: Float = 0f,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateShowMenu(show: Boolean) {
        _uiState.value = _uiState.value.copy(showMenu = show)
    }

    fun updateShowDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDialog = show)
    }

    fun selectClient(client: M2Client) {

    }

    fun clearClientFilter() {

    }

    fun loadClients() {
    }
}
