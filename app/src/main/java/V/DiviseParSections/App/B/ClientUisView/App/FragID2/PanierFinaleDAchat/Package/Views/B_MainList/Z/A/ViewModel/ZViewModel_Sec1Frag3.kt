package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ACentral
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ZViewModel_Sec1Frag3(
    aCentral: ACentral,
) : ViewModel() {
    val uiStateCentralRepositorys = aCentral.getter

    data class UiState_Sec1Frag3(
        val isMinimized: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState_Sec1Frag3())
    val uiState: StateFlow<UiState_Sec1Frag3> = _uiState.asStateFlow()

    fun toggleMinimizedState() {
        _uiState.update { currentState ->
            currentState.copy(isMinimized = !currentState.isMinimized)
        }
    }

}
inline fun Boolean.ifTrue(block: () -> Unit) {
    if (this) block()
}
inline fun Boolean.ifFalse(block: () -> Unit) {
    if (!this) block()
}

enum class ClickUpdate {
    CouleurQua,
    TotalQua
}
