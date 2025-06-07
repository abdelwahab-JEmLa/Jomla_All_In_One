package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

// Helper function to format time in MM:SS format
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

// Helper function to start recording
private fun startRecording(
    context: Context,
    viewModel: ViewModelMessageur,
    uiState: UiState
): Pair<MediaRecorder, File> {
    val outputFile = File(context.filesDir, "voice_${System.currentTimeMillis()}.3gp")

    val mediaRecorder = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        setOutputFile(outputFile.absolutePath)
        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            prepare()
            start()
        } catch (e: IOException) {
            throw IOException("Failed to start recording: ${e.message}")
        }
    }

    return Pair(mediaRecorder, outputFile)
}

// Helper function to stop recording
private fun stopRecording(
    uiState: UiState,
    mediaRecorder: MediaRecorder?,
    context: Context,
    outputFile: File?,
    viewModel: ViewModelMessageur
) {
    try {
        mediaRecorder?.apply {
            stop()
            release()
        }

        // Verify that the file was created and has content
        outputFile?.let { file ->
            if (file.exists() && file.length() > 0) {
                Toast.makeText(
                    context,
                    "Enregistrement sauvegardé: ${file.name}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Erreur: fichier d'enregistrement vide ou inexistant",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    } catch (e: Exception) {
        throw Exception("Failed to stop recording: ${e.message}")
    }
}

@Composable
fun ButtonEnregestrementMessageVocaleEtLeMetreAuStorageGoogle(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }

    // States
    var isRecording by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableStateOf(0) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var outputFile by remember { mutableStateOf<File?>(null) }
    var currentRecordingEtate by remember { mutableStateOf<D_EtateMessageVocale?>(null) }
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

                coroutineScope.launch {
                    if (isRecording) {
                        // Stop recording
                        try {
                            stopRecording(
                                uiState,
                                mediaRecorder,
                                context,
                                outputFile,
                                viewModel,
                            )

                            // Update the recording state to ENVOYER using viewModel's addOrUpdateData
                            currentRecordingEtate?.let { etate ->
                                val updatedEtate = etate.copy(
                                    nom = D_EtateMessageVocale.Nom.ENVOYER,
                                    timestamps = datesHandler.getCurrentTimestamps()
                                )
                                // FIXED: Use viewModel's addOrUpdateData method instead of direct database access
                                viewModel.addOrUpdateData(updatedEtate)
                            }

                            isRecording = false
                            mediaRecorder = null
                            currentRecordingEtate = null
                            recordingTimeSeconds = 0
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Erreur lors de l'arrêt de l'enregistrement: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Start recording
                        try {
                            // Create a new D_EtateMessageVocale with EN_COURT_ENREGESTREMENT state
                            val newEtate = D_EtateMessageVocale(
                                parentMessageVID = System.currentTimeMillis(), // Use timestamp as unique ID
                                nom = D_EtateMessageVocale.Nom.EN_COURT_ENREGESTREMENT,
                                timestamps = datesHandler.getCurrentTimestamps()
                            )

                            // FIXED: Use viewModel's addOrUpdateData method instead of direct database access
                            viewModel.addOrUpdateData(newEtate)
                            currentRecordingEtate = newEtate

                            // Start recording
                            val (recorder, file) = startRecording(
                                context,
                                viewModel,
                                uiState
                            )

                            mediaRecorder = recorder
                            outputFile = file
                            isRecording = true

                            // Start the timer
                            recordingTimeSeconds = 0
                            while (isRecording) {
                                delay(1000)
                                recordingTimeSeconds++
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Erreur lors du démarrage de l'enregistrement: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            isRecording = false
                            currentRecordingEtate = null
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
