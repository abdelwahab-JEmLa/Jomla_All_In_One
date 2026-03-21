package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel

import Application4.App.Modules.Wi.Module.WifiTransferDatas_NewProto
import Application4.App.Modules.Wi.Module.WifiUpdateClientDisplayerStats_NewProto
import EntreApps.Shared.Models.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelPresistantButtonsSec8FWinID1(
    val  aCentralFacade: ACentralFacade,
) : ViewModel() {
    val getter=aCentralFacade.repositorysMainGetter
    val  setterFocusedVarsHandlerFacade =aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
    val  getterFocusedVarsHandlerFacade =aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val  setter =aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
    val appComptComposeRepositoryProtoJuin17 = getter.repo9AppCompt

    data class UiState(
        val setter: FocusedValuesSetter,
        val focusedVarsHandlerFacade: FocusedActiveValuesFacade,
        val getter: FocusedValuesGetter,
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
        getter =aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
        setter =aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter,
        id8BonVentRepository =getter.repo8BonVent,
        hClientRepository =getter.repo2Client,
        zAppComptRepositoryComposable =getter.repo9AppCompt
    ))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun update_M9(data: Z_AppCompt): Unit {
       aCentralFacade.repositorysMainSetter.update_M9AppCompt(data)
    }
}
