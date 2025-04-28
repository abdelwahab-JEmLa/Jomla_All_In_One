package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.Functions

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.MessageurUiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

// Constants for logging
private const val TAG = "MessageVocaleRecorder"

fun startRecording(
    context: Context,
    viewModel: ViewModelMessageur,
    uiState: MessageurUiState,
): Pair<MediaRecorder, File> {
    val lastOrNullkeyIDMessageVocale = uiState.noSqlMessageVocaleList
        .lastOrNull()?.keyIDMessageVocale

    val maxVid = uiState.messageVocaleList.find {
        it.keyID == lastOrNullkeyIDMessageVocale
    }?.vid?.plus(1) ?: 1

    val currentTimeStr = DatesHandler().getDateAndTimString().time

    val newMessageKeyID = "$maxVid->(${currentTimeStr})"

    // Create a consistent voice file ID
    val voiceFileID = "voice_${maxVid}_${System.currentTimeMillis()}"

    val fileName = "$voiceFileID.aac"
    val file = File(context.cacheDir, fileName)

    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }

    recorder.apply {
        try {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(1)
            setAudioSamplingRate(16000)
            setAudioEncodingBitRate(32000)
            setOutputFile(file.absolutePath)

            prepare()
            start()

            viewModel.viewModelScope.launch {
                try {
                    val newMessage = MessageVocale(
                        vid = maxVid,
                        keyID = newMessageKeyID,
                        vocaleKeyID = voiceFileID  // Store the consistent voice file ID
                    )

                    viewModel.appDatabase.messageVocaleDao().insert(newMessage)

                    val newStatue = EtateMessageVocale(
                        parentMessageVID = maxVid,
                        parentMessageKeyID = newMessageKeyID
                    )

                    viewModel.appDatabase.etateMessageVocaleDao().insert(newStatue)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Erreur lors de l'insertion des données: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Erreur lors de la configuration de l'enregistrement: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    return Pair(recorder, file)
}

fun stopRecording(
    uiState: MessageurUiState,
    recorder: MediaRecorder?,
    context: Context,
    file: File?,
    viewModel: ViewModelMessageur,
) {
    try {
        Log.d(TAG, "Stopping recording")
        recorder?.apply {
            stop()
            release()
        }

        file?.let { audioFile ->
            Log.d(TAG, "Recording stopped, file size: ${audioFile.length()} bytes")
            // Upload the voice message to Firebase Storage
            uploadVoiceMessage(
                viewModel = viewModel,
                uiState = uiState,
                file = audioFile,
                context = context
            )
        } ?: run {
            Log.e(TAG, "Error: No file was created during recording")
            Toast.makeText(
                context,
                "Erreur: Aucun fichier n'a été créé pendant l'enregistrement",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error stopping recording: ${e.message}", e)
        Toast.makeText(
            context,
            "Erreur lors de l'arrêt de l'enregistrement: ${e.message}",
            Toast.LENGTH_SHORT
        ).show()
        e.printStackTrace()
    }
}

// Adding formatTime here for consistency
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

// Also update the uploadVoiceMessage function
fun uploadVoiceMessage(
    viewModel: ViewModelMessageur,
    uiState: MessageurUiState,
    file: File,
    context: Context,
) {
    // Get the most recent message
    val lastMessage = uiState.messageVocaleList.maxByOrNull { it.vid }

    if (lastMessage == null) {
        Toast.makeText(
            context,
            "Erreur: Aucun message trouvé dans la base de données",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    // Use the vocaleKeyID directly from the MessageVocale entity
    val voiceFileID = lastMessage.vocaleKeyID

    if (voiceFileID.isEmpty()) {
        Toast.makeText(
            context,
            "Erreur: ID du fichier vocal est vide",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    // Generate filename with the voice file ID
    val fileId = "$voiceFileID.aac"

    val messagesVocalesRef = MessageVocale.storageRef
    val fileRef = messagesVocalesRef.child(fileId)

    fileRef.putFile(android.net.Uri.fromFile(file))
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "Message vocal enregistré avec succès",
                Toast.LENGTH_SHORT
            ).show()

            viewModel.viewModelScope.launch {
                try {

                    // Create new state for the message
                    val newEtate = EtateMessageVocale(
                        parentMessageVID = lastMessage.vid,
                        parentMessageKeyID = lastMessage.keyID,
                        nom = EtateMessageVocale.Nom.ENVOYER
                    )

                    viewModel.appDatabase.etateMessageVocaleDao().insert(newEtate)

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Erreur lors de la mise à jour de l'état du message: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(
                context,
                "Échec de l'enregistrement du message: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}
