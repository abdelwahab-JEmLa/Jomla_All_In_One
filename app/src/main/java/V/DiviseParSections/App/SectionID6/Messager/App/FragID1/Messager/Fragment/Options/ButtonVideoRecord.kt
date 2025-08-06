package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

@Composable
fun ButtonVideoRecord(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur,
    onVideoRecorded: (String) -> Unit
) {      //<--
//TODO(1): fait u^load le fichie au locale aussi 
    val context = LocalContext.current
    val activity = remember { getActivityFromContext(context) }
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }

    var isRecording by remember { mutableStateOf(ScreenRecordingService.isRecording) }
    var isUploading by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableStateOf(0) }
    var currentVideoFile by remember { mutableStateOf<File?>(null) }

    // Screen recording permission launcher
    val screenRecordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        android.util.Log.d("ScreenRecord", "Permission result - Code: ${result.resultCode}, Data: ${result.data}")

        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                // Validate the result data
                val resultData = result.data!!
                android.util.Log.d("ScreenRecord", "Result data extras: ${resultData.extras}")

                // Create the service intent
                val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                    action = ScreenRecordingService.ACTION_START_RECORDING
                    putExtra(ScreenRecordingService.EXTRA_RESULT_CODE, result.resultCode)
                    putExtra(ScreenRecordingService.EXTRA_RESULT_DATA, resultData)
                }

                // Start the service
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

                android.util.Log.d("ScreenRecord", "Service started successfully")

            } catch (e: Exception) {
                android.util.Log.e("ScreenRecord", "Failed to start recording service", e)
                Toast.makeText(context, "Failed to start recording service: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            android.util.Log.w("ScreenRecord", "Permission denied - Code: ${result.resultCode}")
            Toast.makeText(context, "Permission denied for screen recording", Toast.LENGTH_SHORT).show()
        }
    }

    // Set up callback for recording service
    DisposableEffect(Unit) {
        val callback = object : ScreenRecordingService.ScreenRecordingCallback {
            override fun onRecordingStarted(videoFile: File) {
                android.util.Log.d("ScreenRecord", "Recording started: ${videoFile.name}")
                currentVideoFile = videoFile
                isRecording = true
                recordingTimeSeconds = 0

                // Start timer
                coroutineScope.launch {
                    while (isRecording && ScreenRecordingService.isRecording) {
                        delay(1000)
                        recordingTimeSeconds++
                    }
                }
            }

            override fun onRecordingStopped(videoFile: File?) {
                android.util.Log.d("ScreenRecord", "Recording stopped: ${videoFile?.name}")
                isRecording = false
                recordingTimeSeconds = 0

                if (videoFile != null && videoFile.exists()) {
                    isUploading = true

                    coroutineScope.launch {
                        try {
                            android.util.Log.d("VideoUpload", "Starting upload for: ${videoFile.name}")
                            android.util.Log.d("VideoUpload", "File size: ${videoFile.length()} bytes")

                            val fileName = uploadVideoToFirebase(videoFile)
                            isUploading = false

                            onVideoRecorded(fileName)
                            Toast.makeText(context, "Video uploaded successfully!", Toast.LENGTH_SHORT).show()

                            // Clean up local file
                            videoFile.delete()
                        } catch (e: Exception) {
                            isUploading = false
                            android.util.Log.e("VideoUpload", "Upload failed", e)
                            Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    android.util.Log.e("ScreenRecord", "Recording failed - no file created")
                    Toast.makeText(context, "Recording failed - no file created", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onRecordingError(error: String) {
                android.util.Log.e("ScreenRecord", "Recording error: $error")
                isRecording = false
                recordingTimeSeconds = 0
                Toast.makeText(context, "Recording error: $error", Toast.LENGTH_LONG).show()
            }
        }

        ScreenRecordingService.recordingCallback = callback

        onDispose {
            ScreenRecordingService.recordingCallback = null
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isRecording) {
            Text(
                text = formatTime(recordingTimeSeconds),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        }

        if (isUploading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(56.dp)
                    .padding(bottom = 8.dp)
            )
        }

        FloatingActionButton(
            modifier = Modifier.size(56.dp),
            onClick = {
                if (!isRecording) {
                    // Start recording
                    if (activity != null) {
                        try {
                            android.util.Log.d("ScreenRecord", "Requesting screen capture permission")
                            val mediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                            val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
                            screenRecordLauncher.launch(captureIntent)
                        } catch (e: Exception) {
                            android.util.Log.e("ScreenRecord", "Error accessing screen recording", e)
                            Toast.makeText(context, "Error accessing screen recording: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        android.util.Log.e("ScreenRecord", "Activity is null")
                        Toast.makeText(context, "Unable to access activity - try restarting the app", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Stop recording
                    android.util.Log.d("ScreenRecord", "Stopping recording")
                    val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                        action = ScreenRecordingService.ACTION_STOP_RECORDING
                    }
                    context.startService(serviceIntent)
                }
            },
            containerColor = when {
                isUploading -> Color(0xFF4CAF50)
                isRecording -> Color(0xFFFF5722)
                else -> Color(0xFF9C27B0)
            },
            shape = CircleShape
        ) {
            when {
                isUploading -> Icon(
                    painter = painterResource(id = R.drawable.ic_upload),
                    contentDescription = "Uploading video",
                    tint = Color.White
                )
                isRecording -> Icon(
                    painter = painterResource(id = R.drawable.ic_stop),
                    contentDescription = "Stop recording",
                    tint = Color.White
                )
                else -> Icon(
                    painter = painterResource(id = R.drawable.ic_videocam),
                    contentDescription = "Start screen recording",
                    tint = Color.White
                )
            }
        }
    }
}

// Helper function to get Activity from Context
private fun getActivityFromContext(context: Context): Activity? {
    return when (context) {
        is Activity -> context
        is ComponentActivity -> context
        is ContextThemeWrapper -> {
            var baseContext = context.baseContext
            while (baseContext is ContextThemeWrapper && baseContext !is Activity) {
                baseContext = baseContext.baseContext
            }
            baseContext as? Activity
        }
        else -> {
            var currentContext = context
            while (currentContext is android.content.ContextWrapper) {
                if (currentContext is Activity) {
                    return currentContext
                }
                currentContext = currentContext.baseContext
            }
            null
        }
    }
}

private suspend fun uploadVideoToFirebase(videoFile: File): String {
    val storage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = storage.reference
    val videoRef = storageRef.child("VideosMessages/${videoFile.name}")

    // Create metadata with proper content type
    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
        .setContentType("video/mp4")
        .setCustomMetadata("originalName", videoFile.name)
        .setCustomMetadata("uploadTimestamp", System.currentTimeMillis().toString())
        .build()

    val uploadTask = videoRef.putFile(android.net.Uri.fromFile(videoFile), metadata)
    uploadTask.await()

    return videoFile.name
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
