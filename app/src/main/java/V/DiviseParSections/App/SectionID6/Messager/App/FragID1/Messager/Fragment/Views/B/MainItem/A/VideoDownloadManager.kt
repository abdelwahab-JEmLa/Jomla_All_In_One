package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.A

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.io.File

/**
 * Manages video downloads and caching for the messaging system
 * FIXED: Resolved enum naming conflicts and improved error handling
 */
class VideoDownloadManager(private val context: Context) {

    private val _downloadStates = MutableStateFlow<Map<String, String>>(emptyMap())
    val downloadStates: StateFlow<Map<String, String>> = _downloadStates.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Float>> = _downloadProgress.asStateFlow()

    private val videosDir = File(context.filesDir, "downloaded_videos").apply {
        if (!exists()) mkdirs()
    }

    /**
     * Check if video is already downloaded
     */
    fun isVideoDownloaded(videoFileName: String): Boolean {
        val videoFile = File(videosDir, videoFileName)
        val isDownloaded = videoFile.exists() && videoFile.length() > 0

        android.util.Log.d("VideoDownloadManager", "Checking if $videoFileName is downloaded: $isDownloaded")
        android.util.Log.d("VideoDownloadManager", "File path: ${videoFile.absolutePath}")
        android.util.Log.d("VideoDownloadManager", "File exists: ${videoFile.exists()}, Size: ${videoFile.length()} bytes")

        return isDownloaded
    }

    /**
     * Get local video file if it exists
     */
    fun getLocalVideoFile(videoFileName: String): File? {
        val videoFile = File(videosDir, videoFileName)
        val isValid = videoFile.exists() && videoFile.length() > 0

        android.util.Log.d("VideoDownloadManager", "Getting local file for $videoFileName: ${if (isValid) videoFile.absolutePath else "null"}")

        return if (isValid) videoFile else null
    }

    suspend fun downloadVideo(
        videoFileName: String,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            android.util.Log.i("VideoDownloadManager", "🚀 Starting download for: $videoFileName")

            // Check if already downloaded
            getLocalVideoFile(videoFileName)?.let { existingFile ->
                android.util.Log.i("VideoDownloadManager", "✅ File already exists, skipping download")
                onSuccess(existingFile)
                return
            }

            // Update state to downloading
            updateDownloadState(videoFileName, "DOWNLOADING")
            updateDownloadProgress(videoFileName, 0f)

            val storage = FirebaseStorage.getInstance()
            val videoRef = storage.reference.child("VideosMessages/$videoFileName")

            android.util.Log.d("VideoDownloadManager", "📁 Firebase reference: ${videoRef.path}")

            // Verify file exists in Firebase first
            try {
                val metadata = videoRef.metadata.await()
                android.util.Log.d("VideoDownloadManager", """
                    📋 Firebase file info:
                    - Size: ${metadata.sizeBytes} bytes
                    - Content type: ${metadata.contentType}
                    - Created: ${metadata.creationTimeMillis}
                """.trimIndent())
            } catch (metadataError: Exception) {
                android.util.Log.w("VideoDownloadManager", "⚠️ Could not get metadata: ${metadataError.message}")
            }

            // Create local file
            val videoFile = File(videosDir, videoFileName)

            // Ensure parent directory exists
            videoFile.parentFile?.let { parentDir ->
                if (!parentDir.exists()) {
                    val created = parentDir.mkdirs()
                    android.util.Log.d("VideoDownloadManager", "📁 Created parent directory: $created")
                }
            }

            android.util.Log.d("VideoDownloadManager", "💾 Local file path: ${videoFile.absolutePath}")

            // Download with progress tracking
            val downloadTask = videoRef.getFile(videoFile)

            downloadTask.addOnProgressListener { taskSnapshot ->
                val progress = (taskSnapshot.bytesTransferred.toDouble() / taskSnapshot.totalByteCount.toDouble()).toFloat()
                updateDownloadProgress(videoFileName, progress)

                android.util.Log.v("VideoDownloadManager", """
                    📊 Download progress: ${(progress * 100).toInt()}%
                    - Transferred: ${taskSnapshot.bytesTransferred} bytes
                    - Total: ${taskSnapshot.totalByteCount} bytes
                """.trimIndent())
            }

            // Await download completion
            downloadTask.await()

            // Verify downloaded file
            if (!videoFile.exists() || videoFile.length() == 0L) {
                throw Exception("Downloaded file is empty or doesn't exist")
            }

            android.util.Log.i("VideoDownloadManager", """
                ✅ Download completed successfully!
                - File: ${videoFile.absolutePath}
                - Size: ${videoFile.length()} bytes
                - Readable: ${videoFile.canRead()}
            """.trimIndent())

            // Update state to downloaded
            updateDownloadState(videoFileName, "DOWNLOADED")
            updateDownloadProgress(videoFileName, 1f)

            onSuccess(videoFile)

        } catch (e: Exception) {
            android.util.Log.e("VideoDownloadManager", "❌ Download failed for $videoFileName", e)

            updateDownloadState(videoFileName, "ERROR")

            val errorMessage = when {
                e.message?.contains("Object does not exist") == true ->
                    "Video file not found on server"
                e.message?.contains("Network") == true ->
                    "Network connection error"
                e.message?.contains("Permission") == true ->
                    "Permission denied"
                e.message?.contains("Storage") == true ->
                    "Insufficient storage space"
                else ->
                    e.message ?: "Unknown download error"
            }

            onError(errorMessage)
        }
    }

    /**
     * Delete downloaded video file
     */
    fun deleteVideo(videoFileName: String) {
        val videoFile = File(videosDir, videoFileName)
        if (videoFile.exists()) {
            val deleted = videoFile.delete()
            android.util.Log.d("VideoDownloadManager", "🗑️ Deleted $videoFileName: $deleted")

            if (deleted) {
                updateDownloadState(videoFileName, "NOT_DOWNLOADED")
                updateDownloadProgress(videoFileName, 0f)
            }
        }
    }

    /**
     * Get total size of downloaded videos
     */
    fun getTotalDownloadedSize(): Long {
        val totalSize = videosDir.listFiles()?.sumOf { it.length() } ?: 0L
        android.util.Log.d("VideoDownloadManager", "📊 Total downloaded size: ${totalSize / (1024 * 1024)} MB")
        return totalSize
    }

    /**
     * Clear all downloaded videos
     */
    fun clearAllDownloads() {
        val files = videosDir.listFiles()
        val deletedCount = files?.count { it.delete() } ?: 0

        android.util.Log.i("VideoDownloadManager", "🧹 Cleared $deletedCount downloaded videos")

        _downloadStates.value = emptyMap()
        _downloadProgress.value = emptyMap()
    }

    /**
     * Get list of all downloaded video files
     */
    fun getDownloadedVideos(): List<String> {
        return videosDir.listFiles()?.map { it.name } ?: emptyList()
    }

    /**
     * Get download state for a specific video
     */
    fun getDownloadState(videoFileName: String): String {
        return _downloadStates.value[videoFileName] ?: when {
            isVideoDownloaded(videoFileName) -> "DOWNLOADED"
            else -> "NOT_DOWNLOADED"
        }
    }

    /**
     * Get download progress for a specific video
     */
    fun getDownloadProgress(videoFileName: String): Float {
        return _downloadProgress.value[videoFileName] ?: 0f
    }

    private fun updateDownloadState(videoFileName: String, state: String) {
        _downloadStates.value = _downloadStates.value.toMutableMap().apply {
            this[videoFileName] = state
        }
        android.util.Log.d("VideoDownloadManager", "📱 Updated state for $videoFileName: $state")
    }

    private fun updateDownloadProgress(videoFileName: String, progress: Float) {
        _downloadProgress.value = _downloadProgress.value.toMutableMap().apply {
            this[videoFileName] = progress
        }
    }
}

/**
 * FIXED: Removed enum to avoid naming conflicts, using String constants instead
 */
object VideoDownloadStates {
    const val NOT_DOWNLOADED = "NOT_DOWNLOADED"
    const val DOWNLOADING = "DOWNLOADING"
    const val DOWNLOADED = "DOWNLOADED"
    const val ERROR = "ERROR"
}

/**
 * Data class to hold video playback state
 * FIXED: Enhanced with better error handling and time formatting
 */
data class VideoPlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isBuffering: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val bufferedPercentage: Int = 0
) {
    val progress: Float
        get() = if (duration > 0) {
            (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val formattedCurrentTime: String
        get() = formatTime(currentPosition)

    val formattedDuration: String
        get() = formatTime(duration)

    val remainingTime: Long
        get() = (duration - currentPosition).coerceAtLeast(0L)

    val formattedRemainingTime: String
        get() = formatTime(remainingTime)

    private fun formatTime(timeMs: Long): String {
        if (timeMs < 0) return "00:00"

        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / (1000 * 60)) % 60
        val hours = (timeMs / (1000 * 60 * 60))

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    /**
     * Check if the video is in a playable state
     */
    val isPlayable: Boolean
        get() = !hasError && !isLoading && duration > 0

    /**
     * Get a human readable status message
     */
    val statusMessage: String
        get() = when {
            hasError -> "Error: ${errorMessage ?: "Unknown error"}"
            isLoading -> "Loading video..."
            isBuffering -> "Buffering..."
            duration == 0L -> "Preparing video..."
            isPlaying -> "Playing"
            else -> "Paused"
        }
}
