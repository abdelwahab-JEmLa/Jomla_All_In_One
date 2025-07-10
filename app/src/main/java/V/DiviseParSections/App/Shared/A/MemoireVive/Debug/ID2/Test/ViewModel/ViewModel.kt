package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository.Repo11AchatOperation
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GrossistAchatSec12FragID1_ViewModel(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    val getter = aCentralFacade.getRepositorys
    val fVentCouleurOperationRepository = getter.repo10OperationVentCouleur

    // Repository for handling color operations
    val kAchatCouleurOperationRepository = Repo11AchatOperation(
        getterFocusedValues = getter.getterFocusedValues,
        fVentCouleurOperationRepository = fVentCouleurOperationRepository
    )

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
        _uiState.value = _uiState.value.copy(selectedClient = client)
        // Update the repository filter

    }

    fun clearClientFilter() {
        _uiState.value = _uiState.value.copy(selectedClient = null)
        kAchatCouleurOperationRepository.filterQuery.value =
            Repo11AchatOperation.FilterQuery.NO_FILTER
    }

    fun loadClients() {
        // Load clients from repository
        val clients = getter.hClientInfosRepository.getAllClients() // Assuming this method exists
        _uiState.value = _uiState.value.copy(B_ClientInfosProtoJuin3List = clients)
    }
}
