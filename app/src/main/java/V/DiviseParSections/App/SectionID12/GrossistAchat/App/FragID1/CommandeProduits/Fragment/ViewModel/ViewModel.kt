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
        var dialog_Filter_VentPeriod_showDialog: Boolean = true,

        var dialog_Choisire_Grossist_Modularized_showDialog: Boolean = false,
        var dialog_Choisire_Grossist_Modularized_showDialog_Pour_MainScreen: Boolean = false,
        val showMenu: Boolean = false,
        val showDialog: Boolean = false,
        val B_ClientInfosProtoJuin3List: List<M2Client> = emptyList(),
        val mainLoadingProgress: Float = 0f,
        val show_Dialog_filter_Products_Par_Client: Boolean = false, // NEW: Product filter dialog state

    )

    val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Add this method to the ViewModel class
    fun update_show_Dialog_filter_Products_Par_Client(show: Boolean) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            show_Dialog_filter_Products_Par_Client = show
        )
    }
    // Add a method to validate data integrity
    fun validateDataIntegrity(): Boolean {
        return try {
            val achatOperations = aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
            val couleurData = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.datasValue
            val produitData = aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue

            // Check if all referenced data exists
            achatOperations.all { achat ->
                val couleurExists = couleurData.any { it.keyID == achat.parent_M3CouleurProduit_KeyID }
                val produitExists = produitData.any { it.keyID == achat.parent_M1Produit_KeyID }

                couleurExists && produitExists
            }
        } catch (e: Exception) {
            println("Error validating data integrity: ${e.message}")
            false
        }
    }

    fun update_dialog_Filter_VentPeriod_showDialog(
        pour_MainScreen: Boolean = false
    ) {
        _uiState.value = _uiState.value.copy(
            dialog_Filter_VentPeriod_showDialog = pour_MainScreen
        )
    }

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
