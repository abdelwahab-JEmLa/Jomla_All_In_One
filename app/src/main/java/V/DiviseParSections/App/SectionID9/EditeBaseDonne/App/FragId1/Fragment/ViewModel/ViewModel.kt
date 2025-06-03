package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_MasterRepositorys
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.addOrUpdateData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val a_ProduitInfosList: List<A_ProduitInfosProtoJuin3> = emptyList(),
    val c_CategorieProduitInfosList: List<C_CategorieProduitInfos> = emptyList(),
    val mainLoadingProgressPJuin3: Float = 0f,
)

class StartUpFragmentViewModel(
    private val masterRepositorys: A_MasterRepositorys,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        collecteMasterRepositorysDatasAuUiState()
    }

    private fun collecteMasterRepositorysDatasAuUiState() {
        viewModelScope.launch {
            masterRepositorys.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        a_ProduitInfosList = model.repoStateA_ProduitInfos?.modelListFlow
                            ?: emptyList(),
                        c_CategorieProduitInfosList = model.repoStateC_CategorieProduitInfos?.modelListFlow
                            ?: emptyList(),
                        mainLoadingProgressPJuin3 = model.progress
                    )
                }
            }
        }
    }

    fun addOrUpdateCategs(categories: List<C_CategorieProduitInfos>) {
        masterRepositorys.repoC_CategorieProduitInfos.addOrUpdateDatas(categories)
    }

    fun addOrUpdateProduit(data: A_ProduitInfosProtoJuin3) {
        masterRepositorys.repoA_ProduitInfos.addOrUpdateData(data)
    }
}
