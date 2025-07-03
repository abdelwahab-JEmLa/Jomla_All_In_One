package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.BProduitInfosRepository
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.B1CouleurOuGoutProduitDataBaseRepository
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVentRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.ZAppCompt_RepositoryComposable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelMainFastSearchProduitPourVent(
    aCentral: ACentralFacade,
) : ViewModel() {
    val getter = aCentral.getter

    data class UiState(
        val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
        val bProduitInfosRepository: BProduitInfosRepository,
        val searchText: String = "",
        val isLoading: Boolean = false,
        val showAddDialog: Boolean = false,
        val b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository,
        val iD2ClientRepository: HClientRepository,
        val id8BonVentRepository: GBonVentRepository
    )

    private val _uiState = MutableStateFlow(
        UiState(
            bProduitInfosRepository = aCentral.getter.bProduitInfosRepository,
            id8BonVentRepository = aCentral.getter.id8BonVentRepository,
            iD2ClientRepository = aCentral.getter.iD2ClientRepository,
            b1CouleurOuGoutProduitDataBaseRepository = aCentral.getter.b1CouleurOuGoutProduitDataBaseRepository,
            zAppComptRepositoryComposable = aCentral.getter.zAppComptRepositoryComposable,
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

