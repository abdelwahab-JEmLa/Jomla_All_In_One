package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocale
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun handlePlaybackClick(
    isCurrentlyPlaying: Boolean,
    audioHandler: AudioRecorderAndPlayHandler,
    parentD_EtateMessageVocale: D_EtateMessageVocale,
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
                                    val newEtate = D_EtateMessageVocale(
                                        parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                                        nom = D_EtateMessageVocale.Nom.ECOUTE,
                                        timestamps = datesHandler.getCurrentTimestamps(),
                                        idParent_1_5_Vendeur = parentD_EtateMessageVocale.idParent_1_5_Vendeur,
                                        nomParent_1_5_Vendeur = parentD_EtateMessageVocale.nomParent_1_5_Vendeur,
                                        relativeAuDataBase = parentD_EtateMessageVocale.relativeAuDataBase,
                                        parentC3_BonAchateVID = parentD_EtateMessageVocale.parentC3_BonAchateVID
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
