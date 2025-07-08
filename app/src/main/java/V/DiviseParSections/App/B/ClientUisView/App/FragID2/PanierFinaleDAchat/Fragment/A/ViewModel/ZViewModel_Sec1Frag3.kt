package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.CentralFacade
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ZViewModel_Sec1Frag3(
    val aCentral: CentralFacade,
) : ViewModel() {
    val uiStateCentralRepositorys = aCentral.get
    val setter = aCentral.set

    data class UiState_Sec1Frag3(
        val isMinimized: Boolean = true,
        val panieMode: PanieMode = PanieMode.Delivery,
        val filterNonTrouve: Boolean = true,
    )

    private val _uiState = MutableStateFlow(UiState_Sec1Frag3())
    val uiState: StateFlow<UiState_Sec1Frag3> = _uiState.asStateFlow()

    fun toggleMinimizedState() {
        _uiState.update { currentState ->
            currentState.copy(isMinimized = !currentState.isMinimized)
        }
    }

    enum class PanieMode {
        Delivery,
        Vent;

        fun toggle(): PanieMode {
            return when (this) {
                Delivery -> Vent
                Vent -> Delivery
            }
        }
    }

    fun togglePanieMode() { _uiState.update { currentState -> currentState.copy(panieMode = currentState.panieMode.toggle()) } }
    fun toggleEtateDeliveryNonTrouveVentOu(produitKey: String) { setter.toggleEtateDeliveryNonTrouveVentOu(produitKey) }
    fun toggelePanierFilterNonTrouve() { _uiState.update { currentState -> currentState.copy(filterNonTrouve = !currentState.filterNonTrouve) } }
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
