package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.Shared.Repository.MID2ClientRepository.Repository.HClientRepository
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelPresistantButtonsSec8FWinID1(
    central: ACentralFacade,
    val wifiTransferDatas: WifiTransferDatas,
) : ViewModel() {
    val getter=central.getter
    val appComptComposeRepositoryProtoJuin17 = getter.zAppComptRepositoryComposable

    data class UiState(
        val hClientRepository: HClientRepository,
        val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
        val f: Int = 0,
    )
    private val _uiState = MutableStateFlow(UiState(
        hClientRepository=getter.hClientRepository,
        zAppComptRepositoryComposable=getter.zAppComptRepositoryComposable
    ))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun sendOrderAuPresentoireDevice(catalogueBsonID:String): Unit {
        wifiTransferDatas.sendOrderToClientDisplayerT(
            WifiUpdateClientDisplayerStats.NewArregmentColorsJsonStruct
            ,catalogueBsonID
        )
    }
}
