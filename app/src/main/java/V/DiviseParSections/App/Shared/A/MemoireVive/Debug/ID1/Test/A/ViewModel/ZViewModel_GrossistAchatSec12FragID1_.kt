package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelMainFastSearchProduitPourVent(
    aCentral: ACentralFacade,
) : ViewModel() {
    val getter = aCentral.getter

    data class UiState(
        val zAppComptRepositoryComposable: Repo9AppCompt,
        val bProduitInfosRepository: RepoM1ProduitInfos,
        val searchText: String = "",
        val isLoading: Boolean = false,
        val showAddDialog: Boolean = false,
        val b1CouleurOuGoutProduitDataBaseRepository: Repo3CouleurProduitInfos,
        val iD2ClientRepository: Repo2Client,
        val id8BonVentRepository: Repo8BonVent
    )

    private val _uiState = MutableStateFlow(
        UiState(
            bProduitInfosRepository = aCentral.getter.bProduitInfosRepository,
            id8BonVentRepository = aCentral.getter.id8BonVentRepository,
            iD2ClientRepository = aCentral.getter.iD2ClientRepository,
            b1CouleurOuGoutProduitDataBaseRepository = aCentral.getter.b1CouleurOuGoutProduitDataBaseRepository,
            zAppComptRepositoryComposable = aCentral.getter.id9AppComptRepository,
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onSearchTextChange(newText: String) {
        _uiState.value.copy(searchText = newText).also { _uiState.value = it }
    }

    fun onAddNewProduct() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }
}

