package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.SetFocusedVars
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelPresistantButtonsSec8FWinID1(
    val  aCentralFacade: ACentralFacade,
    val wifiTransferDatas: WifiTransferDatas,
) : ViewModel() {
    val getter=aCentralFacade.get
    val  setterFocusedVarsHandlerFacade =aCentralFacade.focusedActiveValuesFacade.set
    val  getterFocusedVarsHandlerFacade =aCentralFacade.focusedActiveValuesFacade.get
    val  setter =aCentralFacade.focusedActiveValuesFacade.set
    val appComptComposeRepositoryProtoJuin17 = getter.repo9AppCompt

    data class UiState(
        val setter: SetFocusedVars,
        val focusedVarsHandlerFacade: FocusedActiveValuesFacade,
        val getter: GetFocusedVars,
        val hClientRepository: Repo2Client,
        val id8BonVentRepository: Repo8BonVent,
        val expandButon: Map.Entry<Button, Boolean>? = null,
        val zAppComptRepositoryComposable: Repo9AppCompt,
        val f: Int = 0,
    ) {
        enum class Button(val nom: String) { ID4("ClientSearchButton") }
    }
    private val _uiState = MutableStateFlow(UiState(
        focusedVarsHandlerFacade =aCentralFacade.focusedActiveValuesFacade,
        getter =aCentralFacade.focusedActiveValuesFacade.get,
        setter =aCentralFacade.focusedActiveValuesFacade.set,
        id8BonVentRepository =getter.repo8BonVent,
        hClientRepository =getter.repo2Client,
        zAppComptRepositoryComposable =getter.repo9AppCompt
    ))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun sendOrderAuPresentoireDevice(catalogueBsonID:String): Unit {
        wifiTransferDatas.sendOrderToClientDisplayerT(
            WifiUpdateClientDisplayerStats.NewArregmentColorsJsonStruct
            ,catalogueBsonID
        )
    }
}
