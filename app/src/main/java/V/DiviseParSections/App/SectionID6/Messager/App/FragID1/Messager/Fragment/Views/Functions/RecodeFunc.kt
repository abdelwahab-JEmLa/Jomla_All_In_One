package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.Functions

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.MessageurUiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Fix for the upsertEtReturnSonNewVid lambda issue
fun startRecording(
    context: Context,
    viewModel: ViewModelMessageur,
): Pair<MediaRecorder, File> {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "voice_$timestamp.aac"  // Utiliser AAC au lieu de 3GP
    val file = File(context.cacheDir, fileName)

    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }

    recorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        // Configuration pour AAC (qualité faible pour petit fichier)
        setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioChannels(1)  // Mono
        setAudioSamplingRate(16000)  // 16kHz - bon pour la voix
        setAudioEncodingBitRate(32000)  // 32kbps - taille réduite mais qualité suffisante pour la voix
        setOutputFile(file.absolutePath)

        try {
            prepare()
            start()

            viewModel.viewModelScope.launch {
                // Créer un nouveau message vocal
                val maxVid = viewModel.appDatabase.messageVocaleDao().getMaxVid() + 1
                val currentTimeStr = DatesHandler().getDateAndTimString().time
                val newMessageKeyID = "$maxVid->(${currentTimeStr})"

                val newMessage = MessageVocale(
                    vid = maxVid,
                    keyID = newMessageKeyID
                )

                // Fixed: Use proper callback technique without trailing lambda
                val newVid =
                    viewModel.appDatabase.messageVocaleDao().upsertEtReturnSonNewVid(newMessage)

                // After getting the newVid, insert the EtateMessageVocale
                viewModel.appDatabase.etateMessageVocaleDao().insert(
                    EtateMessageVocale(
                        parentMessageVID = newVid,
                        parentMessageKeyID = newMessageKeyID
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return Pair(recorder, file)
}

// Fix for the stopRecording and uploadVoiceMessage functions
fun stopRecording(
    uiState: MessageurUiState,
    recorder: MediaRecorder?,
    context: Context,
    file: File?,
    viewModel: ViewModelMessageur,
) {
    try {
        recorder?.apply {
            stop()
            release()
        }

        file?.let { audioFile ->
            // Upload the voice message to Firebase Storage
            uploadVoiceMessage(
                viewModel = viewModel,
                uiState = uiState,
                file = audioFile,
                context = context
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            context,
            "Erreur lors de l'arrêt de l'enregistrement",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun uploadVoiceMessage(
    viewModel: ViewModelMessageur,
    uiState: MessageurUiState,
    file: File,
    context: Context,
) {
    val noSqlMessages = uiState.noSqlMessageVocaleList
    val lastNoSqlMessages = noSqlMessages.lastOrNull()
    val lastStateKeyID = lastNoSqlMessages
        ?.keyIDsChildListEtateMessageVocale?.lastOrNull()

    val messagesVocalesRef = MessageVocale.storageRef

    // Generate a unique filename for the voice message using messageVid
    val fileId = "${lastStateKeyID}.aac"  // Extension AAC
    val fileRef = messagesVocalesRef.child(fileId)

    fileRef.putFile(android.net.Uri.fromFile(file))
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "Message vocal enregistré avec succès",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.viewModelScope.launch {
                // Get the last message vocal and its state
                if (lastStateKeyID != null) {
                    val parentVid = lastStateKeyID
                        .substringBefore("->")
                        .trimStart('(').trimEnd(')')
                        .toLongOrNull()

                    parentVid?.let { vid ->
                        val relatedSqlEtate = uiState
                            .etateMessageVocaleList
                            .find { it.parentMessageVID == vid }

                        relatedSqlEtate?.let { etate ->
                            // Fixed: Proper copying and updating of the EtateMessageVocale
                            val updatedEtate = etate.copy(
                                nom = EtateMessageVocale.Nom.ENVOYER
                            )
                            viewModel.appDatabase.etateMessageVocaleDao().update(updatedEtate)
                        }
                    }
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
