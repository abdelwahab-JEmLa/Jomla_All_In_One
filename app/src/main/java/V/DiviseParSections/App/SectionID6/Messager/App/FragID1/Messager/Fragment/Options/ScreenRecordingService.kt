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
import android.hardware.display.VirtualDisplay
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
    private var virtualDisplay: VirtualDisplay? = null
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
                val videoFormat = intent.getStringExtra("video_format") ?: "mp4"
                val videoCodec = intent.getStringExtra("video_codec") ?: "h264"

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

            // Get display dimensions
            val displayMetrics = getDisplayMetrics()
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            val densityDpi = displayMetrics.densityDpi

            // Ensure dimensions are even numbers (required by some encoders)
            val adjustedWidth = if (width % 2 == 0) width else width - 1
            val adjustedHeight = if (height % 2 == 0) height else height - 1

            android.util.Log.d("ScreenRecording", "Recording dimensions: ${adjustedWidth}x${adjustedHeight}, density: $densityDpi")

            // Create video file with WebM extension (will change to MP4 if WebM fails)
            videoFile = File(filesDir, "screen_record_${System.currentTimeMillis()}.webm")
            android.util.Log.d("ScreenRecording", "Creating video file: ${videoFile!!.name} (WebM preferred, MP4 fallback)")

            // Configure MediaRecorder with ultra-compressed settings
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                configureUltraCompressedWebM(this, hasMicPermission, adjustedWidth, adjustedHeight)
            }

            // Create virtual display
            val surface = mediaRecorder!!.surface
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenRecord",
                adjustedWidth,
                adjustedHeight,
                densityDpi,
                0,
                surface,
                null,
                null
            )

            if (virtualDisplay == null) {
                throw RuntimeException("Failed to create virtual display")
            }

            mediaRecorder?.start()
            isRecording = true

            // Update notification based on actual format used
            val formatInfo = if (videoFile!!.name.endsWith(".webm")) "WebM (compressé)" else "MP4 (compressé)"
            val audioInfo = if (hasMicPermission) " + Audio" else " (vidéo seulement)"
            updateNotification("Enregistrement $formatInfo$audioInfo...")

            videoFile?.let { recordingCallback?.onRecordingStarted(it) }

        } catch (e: Exception) {
            android.util.Log.e("ScreenRecording", "Error setting up media recorder", e)
            recordingCallback?.onRecordingError(e.message ?: "Failed to setup recording")
            cleanupResources()
            stopSelf()
        }
    }

    private fun configureUltraCompressedWebM(
        recorder: MediaRecorder,
        hasMicPermission: Boolean,
        width: Int,
        height: Int
    ) {
        android.util.Log.d("ScreenRecording", "Attempting ultra-compressed WebM configuration")

        // Try WebM first, with MP4 fallback
        if (tryConfigureWebM(recorder, hasMicPermission, width, height)) {
            return
        }

        android.util.Log.w("ScreenRecording", "WebM failed, using MP4 with maximum compression")
        configureUltraCompressedMp4(recorder, hasMicPermission, width, height)
    }

    private fun tryConfigureWebM(
        recorder: MediaRecorder,
        hasMicPermission: Boolean,
        width: Int,
        height: Int
    ): Boolean {
        return try {
            // Check WebM support first
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                android.util.Log.w("ScreenRecording", "WebM not supported on Android < 5.0")
                return false
            }

            android.util.Log.d("ScreenRecording", "Trying WebM configuration...")

            // Set sources
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            if (hasMicPermission) {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            }

            // WebM format
            recorder.setOutputFormat(MediaRecorder.OutputFormat.WEBM)

            // VP8 encoder (more compatible than VP9)
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.VP8)

            // Conservative audio settings for WebM compatibility
            if (hasMicPermission) {
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.VORBIS)
                recorder.setAudioEncodingBitRate(96000) // More conservative 96 kbps
                recorder.setAudioSamplingRate(44100) // Standard sample rate
            }

            // Conservative video settings to avoid prepare() failure
            recorder.setVideoSize(width, height)
            recorder.setVideoFrameRate(20) // Conservative framerate

            // Moderate compression for stability
            val moderateBitrate = calculateModerateBitRate(width, height)
            recorder.setVideoEncodingBitRate(moderateBitrate)

            recorder.setOutputFile(videoFile!!.absolutePath)

            // Try to prepare - this is where it usually fails
            recorder.prepare()

            android.util.Log.d(
                "ScreenRecording",
                "WebM configured successfully: ${width}x${height} @ 20fps, " +
                        "video: ${moderateBitrate/1000}kbps, audio: ${if(hasMicPermission) "96kbps" else "none"}"
            )

            true

        } catch (e: Exception) {
            android.util.Log.w("ScreenRecording", "WebM configuration failed: ${e.message}")

            // Reset recorder state after failed prepare
            try {
                recorder.reset()
            } catch (resetException: Exception) {
                android.util.Log.w("ScreenRecording", "Failed to reset recorder: ${resetException.message}")
            }

            false
        }
    }

    private fun configureUltraCompressedMp4(
        recorder: MediaRecorder,
        hasMicPermission: Boolean,
        width: Int,
        height: Int
    ) {
        try {
            android.util.Log.d("ScreenRecording", "Configuring ultra-compressed MP4 as fallback")

            // Set sources in correct order
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            if (hasMicPermission) {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            }

            // MP4 format (most compatible)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            // H.264 encoder (universal support)
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)

            // Compressed audio settings
            if (hasMicPermission) {
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                recorder.setAudioEncodingBitRate(64000) // Low 64 kbps
                recorder.setAudioSamplingRate(44100)
            }

            // Compressed video settings but conservative enough to work
            recorder.setVideoSize(width, height)
            recorder.setVideoFrameRate(20) // Lower framerate for compression

            // Low bitrate for small files
            val compressedBitrate = calculateCompressedBitRate(width, height)
            recorder.setVideoEncodingBitRate(compressedBitrate)

            // Update filename to MP4 since WebM failed
            val mp4File = File(videoFile!!.parent, videoFile!!.name.replace(".webm", ".mp4"))
            videoFile = mp4File

            recorder.setOutputFile(videoFile!!.absolutePath)
            recorder.prepare()

            android.util.Log.d(
                "ScreenRecording",
                "Ultra-compressed MP4 configured: ${width}x${height} @ 20fps, " +
                        "video: ${compressedBitrate/1000}kbps, audio: ${if(hasMicPermission) "64kbps" else "none"}"
            )

        } catch (e: Exception) {
            android.util.Log.e("ScreenRecording", "Even MP4 configuration failed", e)
            throw e
        }
    }

    private fun calculateModerateBitRate(width: Int, height: Int): Int {
        // Moderate bitrates for WebM stability
        val pixels = width * height
        return when {
            pixels <= 640 * 480 -> 500_000 // 500 kbps
            pixels <= 1280 * 720 -> 800_000 // 800 kbps
            pixels <= 1920 * 1080 -> 1_200_000 // 1.2 Mbps
            else -> 1_800_000 // 1.8 Mbps
        }
    }

    private fun calculateCompressedBitRate(width: Int, height: Int): Int {
        // Compressed but stable bitrates for MP4
        val pixels = width * height
        return when {
            pixels <= 640 * 480 -> 400_000 // 400 kbps
            pixels <= 1280 * 720 -> 700_000 // 700 kbps
            pixels <= 1920 * 1080 -> 1_000_000 // 1 Mbps
            else -> 1_500_000 // 1.5 Mbps
        }
    }

    private fun calculateMinimumBitRate(width: Int, height: Int): Int {
        // Calculate very low bitrate for minimum file size
        val pixels = width * height
        val baseRate = when {
            pixels <= 640 * 480 -> 300_000 // 300 kbps for low res (very compressed)
            pixels <= 1280 * 720 -> 500_000 // 500 kbps for 720p (very compressed)
            pixels <= 1920 * 1080 -> 800_000 // 800 kbps for 1080p (very compressed)
            else -> 1_200_000 // 1.2 Mbps for higher res (still compressed)
        }

        // VP8 with WebM container provides excellent compression
        return baseRate
    }

    private fun getDisplayMetrics(): DisplayMetrics {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val metrics = windowManager.currentWindowMetrics
                val bounds = metrics.bounds
                displayMetrics.widthPixels = bounds.width()
                displayMetrics.heightPixels = bounds.height()
                displayMetrics.densityDpi = resources.displayMetrics.densityDpi
            } catch (e: Exception) {
                // Fall back to older method
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.getMetrics(displayMetrics)
            }
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        return displayMetrics
    }

    private fun stopRecording() {
        android.util.Log.d("ScreenRecordingService", "stopRecording() called")

        try {
            val wasRecording = isRecording
            isRecording = false

            if (mediaRecorder != null) {
                android.util.Log.d("ScreenRecordingService", "Stopping MediaRecorder...")
                try {
                    mediaRecorder?.stop()
                    android.util.Log.d("ScreenRecordingService", "MediaRecorder stopped successfully")
                } catch (e: Exception) {
                    android.util.Log.e("ScreenRecordingService", "Error stopping MediaRecorder", e)
                }
            }

            cleanupResources()

            if (wasRecording) {
                android.util.Log.d("ScreenRecordingService", "Calling callback onRecordingStopped with videoFile: ${videoFile?.name}")
                recordingCallback?.onRecordingStopped(videoFile)
            }

        } catch (e: Exception) {
            android.util.Log.e("ScreenRecordingService", "Error stopping recording", e)
            recordingCallback?.onRecordingError(e.message ?: "Failed to stop recording")
        } finally {
            stopForeground(true)
            stopSelf()
        }
    }

    private fun cleanupResources() {
        try {
            mediaRecorder?.release()
            mediaRecorder = null

            virtualDisplay?.release()
            virtualDisplay = null

            mediaProjection?.stop()
            mediaProjection = null

            android.util.Log.d("ScreenRecordingService", "Resources cleaned up")
        } catch (e: Exception) {
            android.util.Log.e("ScreenRecordingService", "Error cleaning up resources", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            stopRecording()
        } else {
            cleanupResources()
        }
    }
}
