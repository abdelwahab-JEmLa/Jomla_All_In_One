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

// Fixed to ensure consistency between upload and playback
fun startRecording(
    context: Context,
    viewModel: ViewModelMessageur,
    uiState: MessageurUiState,
): Pair<MediaRecorder, File> {
    Log.d(TAG, "Starting recording process")

    val lastOrNullkeyIDMessageVocale = uiState.noSqlMessageVocaleList
        .lastOrNull()?.keyIDMessageVocale

    val maxVid = uiState.messageVocaleList.find {
        it.keyID == lastOrNullkeyIDMessageVocale
    }?.vid?.plus(1) ?: 1

    Log.d(TAG, "Generated maxVid: $maxVid")

    val currentTimeStr = DatesHandler().getDateAndTimString().time

    val newMessageKeyID = "$maxVid->(${currentTimeStr})"
    Log.d(TAG, "Generated new message key ID: $newMessageKeyID")

    // NEW: Create a consistent voice file ID that will be used for both saving and playback
    val voiceFileID = "voice_${maxVid}_${System.currentTimeMillis()}"
    Log.d(TAG, "Generated voice file ID: $voiceFileID")

    val fileName = "$voiceFileID.aac"
    val file = File(context.cacheDir, fileName)
    Log.d(TAG, "Created file for recording: ${file.absolutePath}")

    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }

    recorder.apply {
        try {
            Log.d(TAG, "Configuring media recorder")
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(1)
            setAudioSamplingRate(16000)
            setAudioEncodingBitRate(32000)
            setOutputFile(file.absolutePath)

            Log.d(TAG, "Preparing recorder")
            prepare()
            Log.d(TAG, "Starting recorder")
            start()

            viewModel.viewModelScope.launch {
                try {
                    Log.d(TAG, "Creating new MessageVocale with ID: $newMessageKeyID and voice file ID: $voiceFileID")
                    val newMessage = MessageVocale(
                        vid = maxVid,
                        keyID = newMessageKeyID,
                        vocaleKeyID = voiceFileID  // Store the consistent voice file ID
                    )

                    Log.d(TAG, "Inserting new MessageVocale into database")
                    viewModel.appDatabase.messageVocaleDao().insert(newMessage)

                    Log.d(TAG, "Creating new EtateMessageVocale for parent $newMessageKeyID")
                    val newStatue = EtateMessageVocale(
                        parentMessageVID = maxVid,
                        parentMessageKeyID = newMessageKeyID
                    )

                    Log.d(TAG, "Inserting new EtateMessageVocale into database")
                    viewModel.appDatabase.etateMessageVocaleDao().insert(newStatue)
                    Log.d(TAG, "Successfully inserted all database records")
                } catch (e: Exception) {
                    Log.e(TAG, "Error inserting database records: ${e.message}", e)
                    Toast.makeText(
                        context,
                        "Erreur lors de l'insertion des données: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error configuring or starting recorder: ${e.message}", e)
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

// Updated upload function to use the consistent voice file ID
fun uploadVoiceMessage(
    viewModel: ViewModelMessageur,
    uiState: MessageurUiState,
    file: File,
    context: Context,
) {
    Log.d(TAG, "Starting voice message upload")

    // Get the most recent message
    val lastMessage = uiState.messageVocaleList.maxByOrNull { it.vid }

    if (lastMessage == null) {
        Log.e(TAG, "Error: No messages found in database")
        Toast.makeText(
            context,
            "Erreur: Aucun message trouvé dans la base de données",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    Log.d(TAG, "Found last message with ID: ${lastMessage.keyID}")
    Log.d(TAG, "Using voice file ID for upload: ${lastMessage.vocaleKeyID}")

    // Use the vocaleKeyID directly from the MessageVocale entity
    val voiceFileID = lastMessage.vocaleKeyID

    if (voiceFileID.isEmpty()) {
        Log.e(TAG, "Error: Voice file ID is empty")
        Toast.makeText(
            context,
            "Erreur: ID du fichier vocal est vide",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    // Generate filename with the voice file ID
    val fileId = "$voiceFileID.aac"
    Log.d(TAG, "Generated file ID for upload: $fileId")

    val messagesVocalesRef = MessageVocale.storageRef
    val fileRef = messagesVocalesRef.child(fileId)

    Log.d(TAG, "Uploading file to Firebase Storage: $fileId")

    fileRef.putFile(android.net.Uri.fromFile(file))
        .addOnSuccessListener {
            Log.d(TAG, "Successfully uploaded voice message to Firebase")
            Toast.makeText(
                context,
                "Message vocal enregistré avec succès",
                Toast.LENGTH_SHORT
            ).show()

            viewModel.viewModelScope.launch {
                try {
                    Log.d(TAG, "Updating message state to ENVOYER")

                    // Get the latest state for this message
                    val latestState = uiState.etateMessageVocaleList
                        .filter { it.parentMessageVID == lastMessage.vid }
                        .maxByOrNull { it.vid }

                    // Create new state for the message
                    val newEtate = EtateMessageVocale(
                        parentMessageVID = lastMessage.vid,
                        parentMessageKeyID = lastMessage.keyID,
                        nom = EtateMessageVocale.Nom.ENVOYER
                    )

                    Log.d(TAG, "Inserting updated state for message: ${lastMessage.keyID}")
                    viewModel.appDatabase.etateMessageVocaleDao().insert(newEtate)
                    Log.d(TAG, "Successfully updated message state to ENVOYER")

                } catch (e: Exception) {
                    Log.e(TAG, "Error updating message state: ${e.message}", e)
                    Toast.makeText(
                        context,
                        "Erreur lors de la mise à jour de l'état du message: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.e(TAG, "Failed to upload voice message: ${exception.message}", exception)
            Toast.makeText(
                context,
                "Échec de l'enregistrement du message: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
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
