package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.DebugKey
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler

class ACentralFacade(
    val getter: AGetter,
    val setter: BSetterFacade,
    val modulesCentral : ModulesCentral
)

class ModulesCentral(
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler,
    val debugKey: DebugKey
)
