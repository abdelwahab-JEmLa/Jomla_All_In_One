package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ButtonVideoRecord(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur,
    onVideoRecorded: (String) -> Unit
) {
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
                // Log click action for recording start
                logUserAction("RECORDING_START", context)

                // Validate the result data
                val resultData = result.data!!
                android.util.Log.d("ScreenRecord", "Result data extras: ${resultData.extras}")

                // Create the service intent with WebM format for minimum storage
                val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                    action = ScreenRecordingService.ACTION_START_RECORDING
                    putExtra(ScreenRecordingService.EXTRA_RESULT_CODE, result.resultCode)
                    putExtra(ScreenRecordingService.EXTRA_RESULT_DATA, resultData)
                    putExtra("video_format", "webm") // Use WebM for better compression
                    putExtra("video_codec", "vp8") // VP8 codec for WebM
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
                logUserAction("RECORDING_START_ERROR", context, e.message)
            }
        } else {
            android.util.Log.w("ScreenRecord", "Permission denied - Code: ${result.resultCode}")
            Toast.makeText(context, "Permission denied for screen recording", Toast.LENGTH_SHORT).show()
            logUserAction("RECORDING_PERMISSION_DENIED", context)
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

                logUserAction("RECORDING_ACTUALLY_STARTED", context, videoFile.name)

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

                logUserAction("RECORDING_STOPPED", context, "Duration: $recordingTimeSeconds seconds")

                if (videoFile != null && videoFile.exists()) {
                    isUploading = true

                    coroutineScope.launch {
                        try {
                            android.util.Log.d("VideoUpload", "Starting upload for: ${videoFile.name}")
                            android.util.Log.d("VideoUpload", "File size: ${videoFile.length()} bytes")

                            // 1. Save file locally first
                            val localFile = saveVideoLocally(videoFile, context)
                            logUserAction("VIDEO_SAVED_LOCALLY", context, localFile?.absolutePath)

                            // 2. Upload to Firebase
                            val fileName = uploadVideoToFirebase(videoFile)
                            isUploading = false

                            onVideoRecorded(fileName)
                            Toast.makeText(context, "Video uploaded and saved locally!", Toast.LENGTH_SHORT).show()
                            logUserAction("VIDEO_UPLOAD_SUCCESS", context, fileName)

                            // Clean up temporary file (keep local copy)
                            videoFile.delete()

                        } catch (e: Exception) {
                            isUploading = false
                            android.util.Log.e("VideoUpload", "Upload failed", e)
                            Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                            logUserAction("VIDEO_UPLOAD_ERROR", context, e.message)
                        }
                    }
                } else {
                    android.util.Log.e("ScreenRecord", "Recording failed - no file created")
                    Toast.makeText(context, "Recording failed - no file created", Toast.LENGTH_SHORT).show()
                    logUserAction("RECORDING_FAILED_NO_FILE", context)
                }
            }

            override fun onRecordingError(error: String) {
                android.util.Log.e("ScreenRecord", "Recording error: $error")
                isRecording = false
                recordingTimeSeconds = 0
                Toast.makeText(context, "Recording error: $error", Toast.LENGTH_LONG).show()
                logUserAction("RECORDING_ERROR", context, error)
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
                // Log every button click
                logUserAction("BUTTON_CLICKED", context, if (isRecording) "STOP" else "START")

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
                            logUserAction("SCREEN_RECORDING_ACCESS_ERROR", context, e.message)
                        }
                    } else {
                        android.util.Log.e("ScreenRecord", "Activity is null")
                        Toast.makeText(context, "Unable to access activity - try restarting the app", Toast.LENGTH_LONG).show()
                        logUserAction("ACTIVITY_NULL_ERROR", context)
                    }
                } else {
                    // Stop recording
                    android.util.Log.d("ScreenRecord", "Stopping recording")
                    val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                        action = ScreenRecordingService.ACTION_STOP_RECORDING
                    }
                    context.startService(serviceIntent)
                    logUserAction("STOP_RECORDING_REQUESTED", context)
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

private fun saveVideoLocally(videoFile: File, context: Context): File? {
    return try {
        // Create app-specific directory for videos
        val videosDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "RecordedVideos")
        if (!videosDir.exists()) {
            videosDir.mkdirs()
        }

        // Create local file with timestamp and WebM extension
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val extension = if (videoFile.name.endsWith(".webm")) ".webm" else ".mp4"
        val localFile = File(videosDir, "screen_record_${timestamp}${extension}")

        // Copy the video file
        FileInputStream(videoFile).use { input ->
            FileOutputStream(localFile).use { output ->
                input.copyTo(output)
            }
        }

        android.util.Log.d("VideoSave", "Video saved locally: ${localFile.absolutePath}")
        localFile
    } catch (e: Exception) {
        android.util.Log.e("VideoSave", "Failed to save video locally", e)
        null
    }
}

private fun logUserAction(action: String, context: Context, details: String? = null) {
    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    val logMessage = "[$timestamp] ACTION: $action" + if (details != null) " - Details: $details" else ""

    // Log to Android Log
    android.util.Log.i("UserAction", logMessage)

    // Save to local log file
    try {
        val logDir = File(context.getExternalFilesDir(null), "logs")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }

        val logFile = File(logDir, "user_actions.log")
        logFile.appendText("$logMessage\n")

    } catch (e: Exception) {
        android.util.Log.e("UserAction", "Failed to write to log file", e)
    }
}

private suspend fun uploadVideoToFirebase(videoFile: File): String {
    val storage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = storage.reference
    val videoRef = storageRef.child("VideosMessages/${videoFile.name}")

    // Determine content type based on file extension
    val contentType = when {
        videoFile.name.endsWith(".webm") -> "video/webm"
        videoFile.name.endsWith(".mp4") -> "video/mp4"
        else -> "video/webm" // Default to WebM
    }

    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
        .setContentType(contentType)
        .setCustomMetadata("originalName", videoFile.name)
        .setCustomMetadata("uploadTimestamp", System.currentTimeMillis().toString())
        .setCustomMetadata("format", if (contentType == "video/webm") "webm" else "mp4")
        .setCustomMetadata("codec", if (contentType == "video/webm") "vp8" else "h264")
        .setCustomMetadata("compressionLevel", "HIGH") // Indicate high compression for minimum storage
        .setCustomMetadata("optimizedForSize", "true") // WebM optimized for smaller size
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
