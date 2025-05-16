package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.B.FireBase.ConvertiseurNoSqlToSqlRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.B.FireBase.Model.ProduitNoSqlDataBase
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class UiState(
    val outputModel: ProduitNoSqlDataBase = ProduitNoSqlDataBase(emptyList()),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TarificationViewModel(
    private val convertiseurNoSqlToSqlRepository: ConvertiseurNoSqlToSqlRepository,
) : ViewModel() {
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    init {
        viewModelScope.launch {
            convertiseurNoSqlToSqlRepository.noSqlDataFlow.collectLatest { noSqlData ->
                _uiState.value = _uiState.value.copy(
                    outputModel = noSqlData,
                    isLoading = false
                )
            }
        }
    }

    fun getSqlProduit(id: Long): A_ProduitInfos? {
        return  convertiseurNoSqlToSqlRepository.getProduitInfos(id)
    }
}
