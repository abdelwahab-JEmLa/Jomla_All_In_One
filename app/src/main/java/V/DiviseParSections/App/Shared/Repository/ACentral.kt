package V.DiviseParSections.App.Shared.Repository

import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler

class ACentral(
    val getter: AGetter,
    val setter: BSetter,
    val modulesCentral : ModulesCentral
)

class ModulesCentral(
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler
)
