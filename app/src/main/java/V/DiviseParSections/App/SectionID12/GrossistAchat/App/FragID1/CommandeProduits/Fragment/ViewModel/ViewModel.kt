package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel

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
        var show_Dialog_filter_AChats_Par_Client_Acheteur: Boolean = false,

        var dialog_Choisire_Grossist_Modularized_showDialog: Boolean = false,
        var dialog_Choisire_Grossist_Modularized_showDialog_Pour_MainScreen: Boolean = false,
        val showMenu: Boolean = false,
        val showDialog: Boolean = false,
        val B_ClientInfosProtoJuin3List: List<M2Client> = emptyList(),
        val mainLoadingProgress: Float = 0f,
    )

    val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun update_show_Dialog_filter_AChats_Par_Client_Acheteur(
        pour_MainScreen: Boolean = false
    ) {
        _uiState.value = _uiState.value.copy(
            show_Dialog_filter_AChats_Par_Client_Acheteur = pour_MainScreen
        )
    }
    fun update_dialog_Choisire_Grossist_Modularized_showDialog(
        pour_Autre: Boolean = false,
        pour_MainScreen: Boolean = false
    ) {
        _uiState.value = _uiState.value.copy(
            dialog_Choisire_Grossist_Modularized_showDialog = pour_Autre,
            dialog_Choisire_Grossist_Modularized_showDialog_Pour_MainScreen = pour_MainScreen
        )
    }

    fun updateShowMenu(show: Boolean) {
        _uiState.value = _uiState.value.copy(showMenu = show)
    }

    fun updateShowDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDialog = show)
    }


    fun loadClients() {
    }
}
