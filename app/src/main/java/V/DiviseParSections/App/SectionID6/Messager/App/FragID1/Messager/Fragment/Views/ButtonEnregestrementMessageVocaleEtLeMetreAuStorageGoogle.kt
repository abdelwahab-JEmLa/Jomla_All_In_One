package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.Functions.formatTime
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.Functions.startRecording
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.Functions.stopRecording
import android.Manifest
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
                    val (recorder, file) = startRecording(
                        context,
                        viewModel,
                        uiState
                    )
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
