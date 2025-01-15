package Packages.Z_P3.Ui.Main.ColorItem3.sellerdashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SellerDashBoardViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<SellerDashBoardState> =
        MutableStateFlow(SellerDashBoardState())

    val stateFlow: StateFlow<SellerDashBoardState> = _stateFlow.asStateFlow()


}
