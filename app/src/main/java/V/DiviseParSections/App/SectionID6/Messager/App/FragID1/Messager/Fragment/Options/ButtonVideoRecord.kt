package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
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
    onVideoRecorded: (String) -> Unit ={}
) {
    val context = LocalContext.current
    val activity = remember { getActivityFromContext(context) }
    val coroutineScope = rememberCoroutineScope()

    var isRecording by remember { mutableStateOf(ScreenRecordingService.isRecording) }
    var isUploading by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableStateOf(0) }
    var currentVideoFile by remember { mutableStateOf<File?>(null) }
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Audio permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        logUserAction("MIC_PERMISSION_RESULT", context, "Granted: $isGranted")
        if (!isGranted) {
            Toast.makeText(context, "Permission microphone refusée - enregistrement vidéo seulement", Toast.LENGTH_LONG).show()
        }
    }

    // Screen recording permission launcher
    val screenRecordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        android.util.Log.d("ScreenRecord", "Permission result - Code: ${result.resultCode}")

        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                logUserAction("RECORDING_START", context)

                val resultData = result.data!!
                android.util.Log.d("ScreenRecord", "Starting recording service...")

                // Use WebM format for minimum file size
                val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                    action = ScreenRecordingService.ACTION_START_RECORDING
                    putExtra(ScreenRecordingService.EXTRA_RESULT_CODE, result.resultCode)
                    putExtra(ScreenRecordingService.EXTRA_RESULT_DATA, resultData)
                    putExtra("video_format", "webm") // WebM for minimum size
                    putExtra("video_codec", "vp8") // VP8 codec for best compression
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

                android.util.Log.d("ScreenRecord", "Service started successfully")

            } catch (e: Exception) {
                android.util.Log.e("ScreenRecord", "Failed to start recording service", e)
                Toast.makeText(context, "Failed to start recording: ${e.message}", Toast.LENGTH_LONG).show()
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
                val finalTime = recordingTimeSeconds
                recordingTimeSeconds = 0

                logUserAction("RECORDING_STOPPED", context, "Duration: ${finalTime}s, File: ${videoFile?.name}")

                if (videoFile != null && videoFile.exists()) {
                    android.util.Log.d("VideoUpload", "File exists, starting upload process")
                    isUploading = true

                    coroutineScope.launch {
                        try {
                            android.util.Log.d("VideoUpload", "Starting upload for: ${videoFile.name}, size: ${videoFile.length()} bytes")

                            // Save file locally first
                            val localFile = saveVideoLocally(videoFile, context)
                            logUserAction("VIDEO_SAVED_LOCALLY", context, localFile?.absolutePath)

                            // Upload to Firebase
                            val fileName = uploadVideoToFirebase(videoFile)
                            isUploading = false

                            onVideoRecorded(fileName)
                            Toast.makeText(context, "Vidéo uploadée et sauvée localement!", Toast.LENGTH_SHORT).show()
                            logUserAction("VIDEO_UPLOAD_SUCCESS", context, fileName)

                            // Clean up temporary file
                            if (videoFile.exists()) {
                                videoFile.delete()
                            }

                        } catch (e: Exception) {
                            isUploading = false
                            android.util.Log.e("VideoUpload", "Upload failed", e)
                            Toast.makeText(context, "Échec de l'upload: ${e.message}", Toast.LENGTH_LONG).show()
                            logUserAction("VIDEO_UPLOAD_ERROR", context, e.message)
                        }
                    }
                } else {
                    android.util.Log.e("ScreenRecord", "Recording failed - no file created")
                    Toast.makeText(context, "Échec de l'enregistrement", Toast.LENGTH_SHORT).show()
                    logUserAction("RECORDING_FAILED_NO_FILE", context)
                }
            }

            override fun onRecordingError(error: String) {
                android.util.Log.e("ScreenRecord", "Recording error: $error")
                isRecording = false
                recordingTimeSeconds = 0
                Toast.makeText(context, "Erreur d'enregistrement: $error", Toast.LENGTH_LONG).show()
                logUserAction("RECORDING_ERROR", context, error)
            }
        }

        ScreenRecordingService.recordingCallback = callback

        onDispose {
            android.util.Log.d("ScreenRecord", "Disposing callback")
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
            Text(
                text = "Upload...",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4CAF50)
            )
        }

        FloatingActionButton(
            modifier = Modifier.size(56.dp),
            onClick = {
                if (!isRecording) {
                    // Request audio permission if not granted
                    if (!hasMicPermission) {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }

                    // Start recording
                    activity?.let { act ->
                        try {
                            android.util.Log.d("ScreenRecord", "Requesting screen capture permission")
                            val mediaProjectionManager = act.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                            val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
                            screenRecordLauncher.launch(captureIntent)
                        } catch (e: Exception) {
                            android.util.Log.e("ScreenRecord", "Error accessing screen recording", e)
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            logUserAction("SCREEN_RECORDING_ACCESS_ERROR", context, e.message)
                        }
                    } ?: run {
                        android.util.Log.e("ScreenRecord", "Activity is null")
                        Toast.makeText(context, "Unable to access activity", Toast.LENGTH_LONG).show()
                        logUserAction("ACTIVITY_NULL_ERROR", context)
                    }
                } else {
                    // Stop recording
                    android.util.Log.d("ScreenRecord", "User requested stop recording")

                    val serviceIntent = Intent(context, ScreenRecordingService::class.java).apply {
                        action = ScreenRecordingService.ACTION_STOP_RECORDING
                    }

                    try {
                        context.startService(serviceIntent)
                        android.util.Log.d("ScreenRecord", "Stop service intent sent")
                        logUserAction("STOP_RECORDING_REQUESTED", context)
                        Toast.makeText(context, "Arrêt de l'enregistrement...", Toast.LENGTH_SHORT).show()

                        // Force UI update if service doesn't respond within 3 seconds
                        coroutineScope.launch {
                            delay(3000)
                            if (isRecording && !ScreenRecordingService.isRecording) {
                                android.util.Log.w("ScreenRecord", "Forcing UI state update")
                                isRecording = false
                                recordingTimeSeconds = 0
                            }
                        }

                    } catch (e: Exception) {
                        android.util.Log.e("ScreenRecord", "Failed to send stop intent", e)
                        Toast.makeText(context, "Erreur lors de l'arrêt: ${e.message}", Toast.LENGTH_LONG).show()
                        logUserAction("STOP_RECORDING_ERROR", context, e.message)
                    }
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

// Function to save video locally
private fun saveVideoLocally(videoFile: File, context: Context): File? {
    return try {
        // Create app-specific directory for videos
        val videosDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "RecordedVideos")
        if (!videosDir.exists()) {
            videosDir.mkdirs()
        }

        // Create local file with timestamp
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val extension = when {
            videoFile.name.endsWith(".webm") -> ".webm"
            videoFile.name.endsWith(".mp4") -> ".mp4"
            else -> ".mp4"
        }
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

// Function to log user actions
private fun logUserAction(action: String, context: Context, details: String? = null) {
    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    val logMessage = "[$timestamp] ACTION: $action" + if (details != null) " - Détails: $details" else ""

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
        else -> "video/mp4" // Default to MP4
    }

    // Create metadata
    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
        .setContentType(contentType)
        .setCustomMetadata("originalName", videoFile.name)
        .setCustomMetadata("uploadTimestamp", System.currentTimeMillis().toString())
        .setCustomMetadata("format", if (contentType == "video/mp4") "mp4" else "webm")
        .setCustomMetadata("fileSize", videoFile.length().toString())
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
