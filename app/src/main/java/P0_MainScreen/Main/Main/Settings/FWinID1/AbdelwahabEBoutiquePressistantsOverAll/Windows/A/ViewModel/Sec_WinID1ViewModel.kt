package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_CentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Sec8FWinID1ViewModel(
    val a_CentralDatasHandlerProtoJuin9: A_CentralCompoRepositoryProtoJuin9,
    wifiTransferDatas: WifiTransferDatas,
) : ViewModel() {
    val appComptComposeRepositoryProtoJuin17 = a_CentralDatasHandlerProtoJuin9.appComptComposeRepositoryProtoJuin17

    data class UiState(
        val f: Int = 0,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

}
