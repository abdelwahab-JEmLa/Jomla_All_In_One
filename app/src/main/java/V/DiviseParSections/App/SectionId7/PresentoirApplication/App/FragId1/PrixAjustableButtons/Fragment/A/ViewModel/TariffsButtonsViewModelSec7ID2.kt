package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App._0.Navigation.Screen
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.E_GroupedDataBasesRepositoryNonConnue
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val loadingProgress: Float = 0f,
    val error: String? = null,
    val isDataSyncing: Boolean = false,
    val isInitializing: Boolean = true,
    val affiche_toujoure_tariffs_tournet: Boolean = false,
    val activeFragment_Its_not_FragmentProduitFastSearchDialog: Boolean = true,
    val hasStartedLoading: Boolean = false
)

class TariffsButtonsViewModelSec7ID2(
    val aCentralFacade: ACentralFacade,
    val fragmentNavigationHandler: FragmentNavigationHandler,
    val repo_0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3,
    private val groupedDataBasesRepository: E_GroupedDataBasesRepositoryNonConnue,
    appDatabase: AppDatabase,
    context: Context,
    val repositorysMainSetter_NewProtoPatterns: RepositorysMainSetter_NewProtoPatterns = RepositorysMainSetter_NewProtoPatterns(
        appDatabase = appDatabase,
        context = context
    )
) : ViewModel() {

    // Proper Flow — collected in viewModelScope, never inside a @Composable
    private val allAppComptsFlow = appDatabase.dao_M9AppCompt().getAllFlow()

    // Exposed as StateFlow for the Composable to observe if needed
    private val _activeAppCompt = MutableStateFlow<Z_AppCompt?>(null)
    val activeAppCompt: StateFlow<Z_AppCompt?> = _activeAppCompt.asStateFlow()

    val getter = aCentralFacade.repositorysMainGetter
    val setter = aCentralFacade.repositorysMainSetter


    private val _uiState = MutableStateFlow(UiState())
        val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var loadingJob: Job? = null
    private var bonAchatCollectorJob: Job? = null
    private var produitAcheteOperationCollectorJob: Job? = null
    private var progressJob: Job? = null

    init {
        _uiState.update {
            it.copy(
                isInitializing = true,
                hasStartedLoading = false,
                isDataSyncing = false,
                loadingProgress = 0f
            )
        }

        // Collect active account from DB → sync relevant fields into UiState
        viewModelScope.launch {
            allAppComptsFlow.collect { list ->
                val active = list.find {
                    it.keyID == M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
                }
                _activeAppCompt.update { active }
                _uiState.update {
                    it.copy(
                        affiche_toujoure_tariffs_tournet = active?.affiche_toujoure_tariffs_tournet ?: false
                    )
                }
            }
        }
        // Collect active fragment changes → update UiState flag
        viewModelScope.launch {
            fragmentNavigationHandler.currentFragment.collect { activeFragment ->
                _uiState.update {
                    it.copy(
                        activeFragment_Its_not_FragmentProduitFastSearchDialog =
                            activeFragment == Screen.FragmentProduitFastSearchDialog
                    )
                }
            }
        }

        loadTariffs()
    }

    fun update_m1Produit(new: M01Produit) {
        repositorysMainSetter_NewProtoPatterns.update_M1Produit(new)
    }

    fun updateListRelativeVentCouleurPrixVent(
        m1produitInfos: M01Produit?,
        newPrix: Double,
        listFocusedM10OpeVentCouleurParPrixDifineur: List<M10OperationVentCouleur>
    ) {
        setter.updateListRelativeVentCouleurPrixVentFacade(
            m1produitInfos,
            newPrix
        )
        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
            .clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID()
    }

    private fun loadTariffs() {
        loadingJob?.cancel()
        bonAchatCollectorJob?.cancel()
        produitAcheteOperationCollectorJob?.cancel()
        progressJob?.cancel()

        loadingJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadingProgress = 0f,
                    isDataSyncing = true,
                    hasStartedLoading = true,
                    isInitializing = false,
                    error = null
                )
            }

            try {
                delay(100)

                launch {
                    try {
                        // reserved for future data loading
                    } catch (e: Exception) {
                        // Handle error silently
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error loading tariffs",
                        loadingProgress = 0f,
                        isDataSyncing = false,
                        hasStartedLoading = true
                    )
                }
            }
        }
    }

    fun refreshTariffs() {
        _uiState.update {
            it.copy(
                loadingProgress = 0f,
                isDataSyncing = true,
                hasStartedLoading = false,
                error = null,
            )
        }
        loadTariffs()
    }

    fun getSyncStatus(): String {
        val state = _uiState.value
        return when {
            state.isInitializing -> "Initializing..."
            !state.hasStartedLoading -> "Preparing to load..."
            state.error != null -> "Error: ${state.error}"
            state.isDataSyncing -> "Synchronizing data..."
            state.loadingProgress >= 1f -> "Loading completed"
            else -> "Ready"
        }
    }

    override fun onCleared() {
        super.onCleared()
        loadingJob?.cancel()
        bonAchatCollectorJob?.cancel()
        produitAcheteOperationCollectorJob?.cancel()
        progressJob?.cancel()
    }

    fun deleteVents(parentProduitOldId: Long) {
    }
}
