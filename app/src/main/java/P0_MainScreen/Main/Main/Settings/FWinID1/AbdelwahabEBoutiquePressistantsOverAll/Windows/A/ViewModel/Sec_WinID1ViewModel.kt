package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.ID2ClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Id8BonVentRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Id9AppComptRepository
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
    val appComptComposeRepositoryProtoJuin17 = getter.id9AppComptRepository

    data class UiState(
        val hClientRepository: ID2ClientRepository,
        val expandButon: Map.Entry<Button, Boolean>? = null,
        val zAppComptRepositoryComposable: Id9AppComptRepository,
        val f: Int = 0,
        val id8BonVentRepository: Id8BonVentRepository,
    ) {
        enum class Button(val nom: String) { ID4("ClientSearchButton") }
    }
    private val _uiState = MutableStateFlow(UiState(
        id8BonVentRepository =getter.id8BonVentRepository,
        hClientRepository =getter.iD2ClientRepository,
        zAppComptRepositoryComposable =getter.id9AppComptRepository
    ))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun sendOrderAuPresentoireDevice(catalogueBsonID:String): Unit {
        wifiTransferDatas.sendOrderToClientDisplayerT(
            WifiUpdateClientDisplayerStats.NewArregmentColorsJsonStruct
            ,catalogueBsonID
        )
    }
}
