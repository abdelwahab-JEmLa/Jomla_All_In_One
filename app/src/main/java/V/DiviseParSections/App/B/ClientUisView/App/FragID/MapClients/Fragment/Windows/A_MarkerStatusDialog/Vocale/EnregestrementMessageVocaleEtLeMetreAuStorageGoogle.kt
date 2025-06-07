package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Vocale

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun EnregestrementMessageVocaleEtLeMetreAuStorageGoogle(
    modifier: Modifier = Modifier,
    clientId: Long? = null,
    onVoiceMessageUploaded: (String) -> Unit = {}
) {
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Recording timer display
        if (isRecording) {
            Text(
                text = formatTime(recordingTimeSeconds),
                style = MaterialTheme.typography.bodyLarge,
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
                        mediaRecorder,
                        context,
                        outputFile,
                        onComplete = { file ->
                            uploadVoiceMessage(
                                file,
                                clientId,
                                context,
                                onSuccess = { fileId ->
                                    onVoiceMessageUploaded(fileId)

                                }
                            )
                        }
                    )
                    isRecording = false
                    mediaRecorder = null
                } else {
                    // Start recording
                    val (recorder, file) = startRecording(context)
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
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "Arrêter l'enregistrement" else "Commencer l'enregistrement"
            )
            Text(
                text = if (isRecording) "Arrêter l'enregistrement" else "Message vocal",
                fontSize = 10.sp
            )
        }
    }
}

private fun startRecording(context: Context): Pair<MediaRecorder, File> {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return Pair(recorder, file)
}

private fun stopRecording(
    recorder: MediaRecorder?,
    context: Context,
    file: File?,
    onComplete: (File) -> Unit
) {
    try {
        recorder?.apply {
            stop()
            release()
        }
        file?.let { onComplete(it) }
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
    file: File?,
    clientId: Long?,
    context: Context,
    onSuccess: (String) -> Unit
) {
    if (file == null) return

    val messagesVocalesRef = Firebase.storage.reference
        .child("1_messagesVocales")

    // Generate a unique filename for the voice message
    val fileId = "voice_${clientId}_${UUID.randomUUID()}.aac"  // Extension AAC
    val fileRef = messagesVocalesRef.child(fileId)

    fileRef.putFile(android.net.Uri.fromFile(file))
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "Message vocal enregistré avec succès",
                Toast.LENGTH_SHORT
            ).show()
            onSuccess(fileId)
        }
        .addOnFailureListener { exception ->
            Toast.makeText(
                context,
                "Échec de l'enregistrement du message: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}

// Helper function to format seconds into MM:SS
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
