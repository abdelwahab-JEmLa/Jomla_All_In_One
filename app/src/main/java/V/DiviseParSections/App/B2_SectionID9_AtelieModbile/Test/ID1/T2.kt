package V.DiviseParSections.App.B2_SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.B2_SectionID9_AtelieModbile.Test.Main.B.Models.C3_BonAchate
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun Main(
    modifier: Modifier = Modifier,
    viewModel: ViewModelT2 = koinViewModel()
) {

}

data class UiState(
    val c3_BonAchate: List<C3_BonAchate> = emptyList(),

    val mainLoadingProgressPJuin3: Float = 0f,
)

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
