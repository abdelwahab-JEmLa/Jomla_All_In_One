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
 */
class VideoDownloadManager(private val context: Context) {
    
    private val _downloadStates = MutableStateFlow<Map<String, VideoDownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, VideoDownloadState>> = _downloadStates.asStateFlow()
    
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
        return videoFile.exists() && videoFile.length() > 0
    }
    
    /**
     * Get local video file if it exists
     */
    fun getLocalVideoFile(videoFileName: String): File? {
        val videoFile = File(videosDir, videoFileName)
        return if (videoFile.exists() && videoFile.length() > 0) videoFile else null
    }
    
    /**
     * Download video from Firebase Storage
     */
    suspend fun downloadVideo(
        videoFileName: String,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Check if already downloaded
            getLocalVideoFile(videoFileName)?.let { existingFile ->
                onSuccess(existingFile)
                return
            }
            
            // Update state to downloading
            updateDownloadState(videoFileName, VideoDownloadState.DOWNLOADING)
            updateDownloadProgress(videoFileName, 0f)
            
            val storage = FirebaseStorage.getInstance()
            val videoRef = storage.reference.child("VideosMessages/$videoFileName")
            
            // Create local file
            val videoFile = File(videosDir, videoFileName)
            
            // Download with progress tracking
            val downloadTask = videoRef.getFile(videoFile)
            
            downloadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat() / 100f
                updateDownloadProgress(videoFileName, progress)
            }
            
            downloadTask.await()
            
            // Update state to downloaded
            updateDownloadState(videoFileName, VideoDownloadState.DOWNLOADED)
            updateDownloadProgress(videoFileName, 1f)
            
            onSuccess(videoFile)
            
        } catch (e: Exception) {
            updateDownloadState(videoFileName, VideoDownloadState.ERROR)
            onError(e.message ?: "Download failed")
        }
    }
    
    /**
     * Delete downloaded video file
     */
    fun deleteVideo(videoFileName: String) {
        val videoFile = File(videosDir, videoFileName)
        if (videoFile.exists()) {
            videoFile.delete()
            updateDownloadState(videoFileName, VideoDownloadState.NOT_DOWNLOADED)
            updateDownloadProgress(videoFileName, 0f)
        }
    }
    
    /**
     * Get total size of downloaded videos
     */
    fun getTotalDownloadedSize(): Long {
        return videosDir.listFiles()?.sumOf { it.length() } ?: 0L
    }
    
    /**
     * Clear all downloaded videos
     */
    fun clearAllDownloads() {
        videosDir.listFiles()?.forEach { it.delete() }
        _downloadStates.value = emptyMap()
        _downloadProgress.value = emptyMap()
    }
    
    private fun updateDownloadState(videoFileName: String, state: VideoDownloadState) {
        _downloadStates.value = _downloadStates.value.toMutableMap().apply {
            this[videoFileName] = state
        }
    }
    
    private fun updateDownloadProgress(videoFileName: String, progress: Float) {
        _downloadProgress.value = _downloadProgress.value.toMutableMap().apply {
            this[videoFileName] = progress
        }
    }
}

enum class VideoDownloadState {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    ERROR
}

/**
 * Data class to hold video playback state
 */
data class VideoPlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isBuffering: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
) {
    val progress: Float
        get() = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
    
    val formattedCurrentTime: String
        get() = formatTime(currentPosition)
    
    val formattedDuration: String
        get() = formatTime(duration)
    
    private fun formatTime(timeMs: Long): String {
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / (1000 * 60)) % 60
        val hours = (timeMs / (1000 * 60 * 60))
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}
