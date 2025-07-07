package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.DebugKey
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.SetFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.MainRepositorysGetterFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.MainRepositorysSetterFacade
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler

class ACentralFacade(
    val mainRepositorysGetterFacade: MainRepositorysGetterFacade,
    val mainRepositorysSetterFacade: MainRepositorysSetterFacade,
    val focusedVarsHandlerFacade: FocusedVarsHandlerFacade,
    val modulesCentral: ModulesCentral
)

class FocusedVarsHandlerFacade(val get: GetFocusedVars, val set: SetFocusedVars)

class ModulesCentral(
    val fragmentNavigationHandler: FragmentNavigationHandler,
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler,
    val debugKey: DebugKey
)
