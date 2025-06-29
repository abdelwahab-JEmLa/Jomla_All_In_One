package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.ACentral
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ZViewModel_Sec1Frag3(
    aCentral: ACentral,
) : ViewModel() {
    val uiStateCentralRepositorys = aCentral.getter
    val setter = aCentral.setter

    data class UiState_Sec1Frag3(
        val isMinimized: Boolean = true,
        val panieMode: PanieMode = PanieMode.Delivery,
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
        Vent, ;

        fun toggle(): Unit {}
    }

    fun toggleEtateDeliveryNonTrouveVentOu(produitKey:String): Unit {
           setter.toggleEtateDeliveryNonTrouveVentOu(produitKey)
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
