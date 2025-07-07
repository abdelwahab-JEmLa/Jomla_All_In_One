package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.DebugKey
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler

class ACentralFacade(
    val getter: AGetter,
    val focusedVarsHandlerFacade: FocusedVarsHandlerFacade,
    val setter: MainSetterFacade,
    val modulesCentral: ModulesCentral
)

class ModulesCentral(
    val fragmentNavigationHandler: FragmentNavigationHandler,
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler,
    val debugKey: DebugKey
)
