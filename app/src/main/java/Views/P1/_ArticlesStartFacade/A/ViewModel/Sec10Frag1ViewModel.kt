package Views.P1._ArticlesStartFacade.A.ViewModel

import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_CentralCompoRepositoryProtoJuin9
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Sec10Frag1ViewModel(
    val a_CentralDatasHandlerProtoJuin9: A_CentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    data class UiState(val f: Int = 0, )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
