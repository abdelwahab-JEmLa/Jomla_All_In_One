package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun handlePlaybackClick(
    isCurrentlyPlaying: Boolean,
    audioHandler: AudioRecorderAndPlayHandler,
    parentD_EtateMessageVocale: M17MessageVocale,
    isListened: Boolean,
    viewModel: ViewModelMessageur,
    datesHandler: DatesHandler,
    context: Context,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        when {
            isCurrentlyPlaying -> {
                val stopResult = audioHandler.stopPlayback()
                if (stopResult.isFailure) {
                    Toast.makeText(
                        context,
                        "Erreur lors de l'arrêt: ${stopResult.exceptionOrNull()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> {
                val playResult = audioHandler.startPlayback(
                    context = context,
                    parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                    onPlaybackComplete = {
                        if (!isListened) {
                            coroutineScope.launch {
                                try {
                                    val newEtate = M17MessageVocale(
                                        parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                                        etate = M17MessageVocale.Etate.ECOUTE,
                                        timestamps = datesHandler.getCurrentTimestamps(),
                                        parent_M9AppCompt_KeyID = parentD_EtateMessageVocale.parent_M9AppCompt_KeyID,
                                        parent_M9AppCompt_DebugInfos = parentD_EtateMessageVocale.parent_M9AppCompt_DebugInfos,
                                        relativeAuDataBase = parentD_EtateMessageVocale.relativeAuDataBase,
                                        parent_M8BonVent_KeyID = parentD_EtateMessageVocale.parent_M8BonVent_KeyID
                                    )
                                    viewModel.addOrUpdateData(newEtate)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    },
                    onPlaybackError = { errorMessage ->
                        Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )

                if (playResult.isFailure) {
                    Toast.makeText(
                        context,
                        "Erreur lors du démarrage: ${playResult.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
