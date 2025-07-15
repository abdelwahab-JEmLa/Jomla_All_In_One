package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelMainFastSearchProduitPourVent(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    val getter = aCentralFacade.repositorysMainGetter

    sealed class RoleDefinieParSourceACetteFragment() {
        data object AfficheSearchAllProduits : RoleDefinieParSourceACetteFragment()
        data class SearchProduit(val produit: ArticlesBasesStatsTable) : RoleDefinieParSourceACetteFragment()
    }

    data class UiState(
        val zAppComptRepositoryComposable: Repo9AppCompt,
        val bProduitInfosRepository: RepoM1Produit,
        val searchText: String = "",
        val isLoading: Boolean = false,
        val showAddDialog: Boolean = false,
        val b1CouleurOuGoutProduitDataBaseRepository: Repo3CouleurProduitInfos,
        val iD2ClientRepository: Repo2Client,
        val id8BonVentRepository: Repo8BonVent
    )

    private val _uiState = MutableStateFlow(
        UiState(
            bProduitInfosRepository = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
            id8BonVentRepository = aCentralFacade.repositorysMainGetter.repo8BonVent,
            iD2ClientRepository = aCentralFacade.repositorysMainGetter.repo2Client,
            b1CouleurOuGoutProduitDataBaseRepository = aCentralFacade.repositorysMainGetter.repo3CouleurProduitInfos,
            zAppComptRepositoryComposable = aCentralFacade.repositorysMainGetter.repo9AppCompt,
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

