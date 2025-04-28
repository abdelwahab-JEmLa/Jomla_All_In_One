package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Functions.formatTime
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.MessageurUiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ButtonEnregestrementMessageVocaleEtLeMetreAuStorageGoogle(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur,
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // States
    var isRecording by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableStateOf(0) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var outputFile by remember { mutableStateOf<File?>(null) }
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Request permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordPermission = isGranted
        if (isGranted) {
            // Can start recording now
        } else {
            Toast.makeText(
                context,
                "Permission d'enregistrement nécessaire pour cette fonctionnalité",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Clean up the recorder when the component is disposed
    DisposableEffect(Unit) {
        onDispose {
            mediaRecorder?.apply {
                try {
                    if (isRecording) {
                        stop()
                    }
                    release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mediaRecorder = null
        }
    }

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Recording timer display
        if (isRecording) {
            Text(
                text = formatTime(recordingTimeSeconds),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        // Record/Stop button
        FilledTonalButton(
            onClick = {
                if (!hasRecordPermission) {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    return@FilledTonalButton
                }

                if (isRecording) {
                    // Stop recording
                    stopRecording(
                        uiState,
                        mediaRecorder,
                        context,
                        outputFile,
                        viewModel,
                    )
                    isRecording = false
                    mediaRecorder = null
                } else {
                    // Start recording
                    val (recorder, file) = startRecording(context, viewModel)
                    mediaRecorder = recorder
                    outputFile = file
                    isRecording = true

                    // Start the timer
                    coroutineScope.launch {
                        recordingTimeSeconds = 0
                        while (isRecording) {
                            delay(1000)
                            recordingTimeSeconds++
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "Arrêter l'enregistrement" else "Commencer l'enregistrement"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isRecording) "Arrêter l'enregistrement" else "Message vocal",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
// Fix for the upsertEtReturnSonNewVid lambda issue
private fun startRecording(
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
                val newVid = viewModel.appDatabase.messageVocaleDao().upsertEtReturnSonNewVid(newMessage)

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
private fun stopRecording(
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

private fun uploadVoiceMessage(
    viewModel: ViewModelMessageur,
    uiState: MessageurUiState,
    file: File,
    context: Context
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
