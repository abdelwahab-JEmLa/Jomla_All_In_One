package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.DebugKey
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetterFocusedValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.SetFocusedVars
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.Get
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.Set
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler

class ACentralFacade(
    val getRepositorys: Get,
    val setRepositorys: Set,
    val focusedActiveValuesFacade: FocusedActiveValuesFacade,
    val modulesCentral: ModulesCentral
)

class FocusedActiveValuesFacade(val getterFocusedValues: GetterFocusedValues, val set: SetFocusedVars)

class ModulesCentral(
    val recordingHandler: IRecordingHandler,
    val fragmentNavigationHandler: FragmentNavigationHandler,
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler,
    val debugKey: DebugKey
)
