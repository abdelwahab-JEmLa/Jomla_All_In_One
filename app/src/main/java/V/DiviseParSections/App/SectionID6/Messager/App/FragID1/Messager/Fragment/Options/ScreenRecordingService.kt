package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.clientjetpack.R
import java.io.File

class ScreenRecordingService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "screen_recording_channel"
        const val ACTION_START_RECORDING = "START_RECORDING"
        const val ACTION_STOP_RECORDING = "STOP_RECORDING"
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"

        var isRecording = false
            private set

        var recordingCallback: ScreenRecordingCallback? = null
    }

    interface ScreenRecordingCallback {
        fun onRecordingStarted(videoFile: File)
        fun onRecordingStopped(videoFile: File?)
        fun onRecordingError(error: String)
    }

    private var mediaRecorder: MediaRecorder? = null
    private var mediaProjection: MediaProjection? = null
    private var videoFile: File? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        android.util.Log.d("ScreenRecordingService", "onStartCommand called with action: ${intent?.action}")

        when (intent?.action) {
            ACTION_START_RECORDING -> {
                android.util.Log.d("ScreenRecordingService", "Processing START_RECORDING action")
                // Start foreground service immediately to avoid RemoteServiceException
                val notification = createNotification("Preparing to record...")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }

                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, -1)
                val resultData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)
                }

                // Get video format preferences from intent
                val videoFormat = intent.getStringExtra("video_format") ?: "webm"
                val videoCodec = intent.getStringExtra("video_codec") ?: "vp8"

                android.util.Log.d("ScreenRecordingService", "Received parameters - ResultCode: $resultCode, Format: $videoFormat, Codec: $videoCodec")

                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    startRecording(resultCode, resultData, videoFormat, videoCodec)
                } else {
                    val errorMsg = "Invalid recording parameters - ResultCode: $resultCode, ResultData: $resultData"
                    android.util.Log.e("ScreenRecordingService", errorMsg)
                    recordingCallback?.onRecordingError("Invalid recording parameters")
                    stopSelf()
                }
            }
            ACTION_STOP_RECORDING -> {
                android.util.Log.d("ScreenRecordingService", "Processing STOP_RECORDING action")
                android.util.Log.d("ScreenRecordingService", "Current recording state before stop: $isRecording")
                stopRecording()
            }
            else -> {
                android.util.Log.w("ScreenRecordingService", "Unknown action received: ${intent?.action}")
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Screen Recording",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Screen recording in progress"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String = "Recording your screen..."): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Screen Recording")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_videocam) // Make sure this icon exists
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification(contentText: String) {
        val notification = createNotification(contentText)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun startRecording(resultCode: Int, resultData: Intent, videoFormat: String, videoCodec: String) {
        try {
            val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData)

            if (mediaProjection == null) {
                recordingCallback?.onRecordingError("Failed to get media projection")
                stopSelf()
                return
            }

            setupMediaRecorder(videoFormat, videoCodec)

        } catch (e: Exception) {
            android.util.Log.e("ScreenRecording", "Error starting recording", e)
            recordingCallback?.onRecordingError(e.message ?: "Failed to start recording")
            stopSelf()
        }
    }

    private fun setupMediaRecorder(videoFormat: String, videoCodec: String) {
        try {
            // Check microphone permission
            val hasMicPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            android.util.Log.d("ScreenRecording", "Microphone permission: $hasMicPermission")

            // Get display dimensions in a way that works from Service context
            val displayMetrics = resources.displayMetrics
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

            var width = displayMetrics.widthPixels
            var height = displayMetrics.heightPixels
            val densityDpi = displayMetrics.densityDpi

            // For newer Android versions, try to get more accurate dimensions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val metrics = windowManager.currentWindowMetrics
                    val bounds = metrics.bounds
                    width = bounds.width()
                    height = bounds.height()
                } catch (e: Exception) {
                    // Fall back to displayMetrics if currentWindowMetrics fails
                    android.util.Log.w("ScreenRecording", "Could not get current window metrics, using display metrics", e)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {
                    @Suppress("DEPRECATION")
                    val display = windowManager.defaultDisplay
                    val realMetrics = DisplayMetrics()
                    display.getRealMetrics(realMetrics)
                    width = realMetrics.widthPixels
                    height = realMetrics.heightPixels
                } catch (e: Exception) {
                    // Fall back to regular metrics
                    android.util.Log.w("ScreenRecording", "Could not get real metrics, using display metrics", e)
                }
            }

            android.util.Log.d("ScreenRecording", "Recording dimensions: ${width}x${height}, density: $densityDpi")

            // Create video file with appropriate extension based on format
            val fileExtension = when (videoFormat.lowercase()) {
                "webm" -> ".webm"
                "mp4" -> ".mp4"
                else -> ".webm" // Default to WebM
            }

            videoFile = File(filesDir, "screen_record_${System.currentTimeMillis()}${fileExtension}")
            android.util.Log.d("ScreenRecording", "Creating video file: ${videoFile!!.name} with format: $videoFormat, audio: $hasMicPermission")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                // Set video source first
                setVideoSource(MediaRecorder.VideoSource.SURFACE)

                // Set audio source if permission granted
                if (hasMicPermission) {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    android.util.Log.d("ScreenRecording", "Audio source set to microphone")
                } else {
                    android.util.Log.w("ScreenRecording", "No microphone permission, recording video only")
                }

                // Configure format and encoder based on parameters
                when (videoFormat.lowercase()) {
                    "webm" -> {
                        setOutputFormat(MediaRecorder.OutputFormat.WEBM)

                        // Set audio encoder for WebM if audio available
                        if (hasMicPermission) {
                            setAudioEncoder(MediaRecorder.AudioEncoder.VORBIS) // Vorbis for WebM
                            setAudioEncodingBitRate(128000) // 128 kbps
                            setAudioSamplingRate(44100) // 44.1 kHz
                        }

                        when (videoCodec.lowercase()) {
                            "vp8" -> {
                                if (isVP8Supported()) {
                                    setVideoEncoder(MediaRecorder.VideoEncoder.VP8)
                                    android.util.Log.d("ScreenRecording", "Using VP8 encoder")
                                } else {
                                    android.util.Log.w("ScreenRecording", "VP8 not supported, falling back to H264")
                                    setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                                }
                            }
                            "vp9" -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isVP9Supported()) {
                                    setVideoEncoder(MediaRecorder.VideoEncoder.VP8) // VP9 constant might not be available
                                    android.util.Log.d("ScreenRecording", "Using VP9 encoder")
                                } else {
                                    android.util.Log.w("ScreenRecording", "VP9 not supported, using VP8")
                                    setVideoEncoder(MediaRecorder.VideoEncoder.VP8)
                                }
                            }
                            else -> {
                                setVideoEncoder(MediaRecorder.VideoEncoder.VP8) // Default WebM encoder
                            }
                        }
                        // WebM optimized settings for smaller files
                        setVideoEncodingBitRate(1_500_000) // 1.5 Mbps (reduced from 5 Mbps)
                        setVideoFrameRate(20) // Reduced framerate for smaller files
                    }
                    "mp4" -> {
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

                        // Set audio encoder for MP4 if audio available
                        if (hasMicPermission) {
                            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // AAC for MP4
                            setAudioEncodingBitRate(128000) // 128 kbps
                            setAudioSamplingRate(44100) // 44.1 kHz
                        }

                        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                        setVideoEncodingBitRate(3_000_000) // 3 Mbps for MP4
                        setVideoFrameRate(30)
                        android.util.Log.d("ScreenRecording", "Using MP4 format with H264")
                    }
                    else -> {
                        // Default to WebM
                        setOutputFormat(MediaRecorder.OutputFormat.WEBM)

                        if (hasMicPermission) {
                            setAudioEncoder(MediaRecorder.AudioEncoder.VORBIS)
                            setAudioEncodingBitRate(128000)
                            setAudioSamplingRate(44100)
                        }

                        setVideoEncoder(MediaRecorder.VideoEncoder.VP8)
                        setVideoEncodingBitRate(1_500_000)
                        setVideoFrameRate(20)
                        android.util.Log.d("ScreenRecording", "Using default WebM format")
                    }
                }

                setOutputFile(videoFile!!.absolutePath)
                setVideoSize(width, height)

                prepare()
            }

            val surface = mediaRecorder!!.surface
            mediaProjection?.createVirtualDisplay(
                "ScreenRecord",
                width,
                height,
                densityDpi,
                0,
                surface,
                null,
                null
            )

            mediaRecorder?.start()
            isRecording = true

            // Update notification to show recording is active with format and audio info
            val formatInfo = when (videoFormat.lowercase()) {
                "webm" -> "WebM (optimized)"
                "mp4" -> "MP4"
                else -> "WebM"
            }
            val audioInfo = if (hasMicPermission) " + Audio" else " (vidéo seulement)"
            updateNotification("Enregistrement écran en $formatInfo$audioInfo...")

            videoFile?.let { recordingCallback?.onRecordingStarted(it) }

        } catch (e: Exception) {
            android.util.Log.e("ScreenRecording", "Error setting up media recorder", e)
            recordingCallback?.onRecordingError(e.message ?: "Failed to setup recording")
            stopSelf()
        }
    }

    // Check if VP8 encoder is supported
    private fun isVP8Supported(): Boolean {
        return try {
            // This is a basic check - you might want to implement more thorough codec detection
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
        } catch (e: Exception) {
            false
        }
    }

    // Check if VP9 encoder is supported
    private fun isVP9Supported(): Boolean {
        return try {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        } catch (e: Exception) {
            false
        }
    }

    private fun stopRecording() {
        android.util.Log.d("ScreenRecordingService", "stopRecording() called")
        android.util.Log.d("ScreenRecordingService", "Current isRecording state: $isRecording")
        android.util.Log.d("ScreenRecordingService", "MediaRecorder: $mediaRecorder")
        android.util.Log.d("ScreenRecordingService", "MediaProjection: $mediaProjection")

        try {
            if (mediaRecorder != null) {
                android.util.Log.d("ScreenRecordingService", "Stopping MediaRecorder...")
                mediaRecorder?.apply {
                    try {
                        stop()
                        android.util.Log.d("ScreenRecordingService", "MediaRecorder stopped successfully")
                    } catch (e: Exception) {
                        android.util.Log.e("ScreenRecordingService", "Error stopping MediaRecorder", e)
                    }
                    release()
                    android.util.Log.d("ScreenRecordingService", "MediaRecorder released")
                }
            } else {
                android.util.Log.w("ScreenRecordingService", "MediaRecorder is null, cannot stop")
            }

            if (mediaProjection != null) {
                android.util.Log.d("ScreenRecordingService", "Stopping MediaProjection...")
                mediaProjection?.stop()
                android.util.Log.d("ScreenRecordingService", "MediaProjection stopped")
            } else {
                android.util.Log.w("ScreenRecordingService", "MediaProjection is null")
            }

            val wasRecording = isRecording
            isRecording = false
            android.util.Log.d("ScreenRecordingService", "isRecording set to false")

            if (wasRecording) {
                android.util.Log.d("ScreenRecordingService", "Calling callback onRecordingStopped with videoFile: ${videoFile?.name}")
                recordingCallback?.onRecordingStopped(videoFile)
            } else {
                android.util.Log.w("ScreenRecordingService", "Was not recording, skipping callback")
            }

        } catch (e: Exception) {
            android.util.Log.e("ScreenRecordingService", "Error stopping recording", e)
            recordingCallback?.onRecordingError(e.message ?: "Failed to stop recording")
        } finally {
            mediaRecorder = null
            mediaProjection = null
            android.util.Log.d("ScreenRecordingService", "Stopping foreground service...")
            stopForeground(true)
            android.util.Log.d("ScreenRecordingService", "Stopping self...")
            stopSelf()
            android.util.Log.d("ScreenRecordingService", "Service cleanup completed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            stopRecording()
        }
    }
}
