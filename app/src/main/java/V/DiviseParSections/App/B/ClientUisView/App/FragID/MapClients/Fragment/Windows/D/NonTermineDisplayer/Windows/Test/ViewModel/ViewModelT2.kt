package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.ViewModel

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelT2(
    val groupeRepositorysProtoAvJuin3: GroupeRepositorysProtoAvJuin3,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val c3_BonAchate_List = groupeRepositorysProtoAvJuin3.repositorys_Model
        .c3_BonAchate_Repository.modelDatasSnapList

    init {
        collecteMasterRepositorysDatasAuUiState()
    }

    private fun collecteMasterRepositorysDatasAuUiState() {
        viewModelScope.launch {
          /*  masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        a_ProduitInfosList = model.repoStateA_ProduitInfos?.modelListFlow
                            ?: emptyList(),
                        c_CategorieProduitInfosList = model.repoStateC_CategorieProduitInfos?.modelListFlow
                            ?: emptyList(),
                        mainLoadingProgressPJuin3 = model.progress
                    )
                }
            }   */
        }
    }
    }
