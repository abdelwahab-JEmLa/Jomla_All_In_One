package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.A// Additional debugging and diagnostic functions for video playback issues

/**
 * Comprehensive video file validation
 */
fun validateVideoFile(file: File): VideoFileValidation {
    val validation = VideoFileValidation()
    
    try {
        android.util.Log.d("VideoValidation", "=== VALIDATING VIDEO FILE ===")
        android.util.Log.d("VideoValidation", "File path: ${file.absolutePath}")
        
        // Basic file checks
        validation.exists = file.exists()
        validation.canRead = file.canRead()
        validation.size = file.length()
        validation.isFile = file.isFile
        validation.parentExists = file.parentFile?.exists() ?: false
        
        android.util.Log.d("VideoValidation", "File exists: ${validation.exists}")
        android.util.Log.d("VideoValidation", "Can read: ${validation.canRead}")
        android.util.Log.d("VideoValidation", "Size: ${validation.size} bytes")
        android.util.Log.d("VideoValidation", "Is file: ${validation.isFile}")
        android.util.Log.d("VideoValidation", "Parent exists: ${validation.parentExists}")
        
        if (validation.exists && validation.size > 0) {
            // Try to determine file type
            val fileName = file.name.lowercase()
            validation.hasVideoExtension = fileName.endsWith(".mp4") || 
                                         fileName.endsWith(".avi") || 
                                         fileName.endsWith(".mov") || 
                                         fileName.endsWith(".mkv") ||
                                         fileName.endsWith(".webm")
            
            android.util.Log.d("VideoValidation", "Has video extension: ${validation.hasVideoExtension}")
            
            // Try to read first few bytes to check file header
            try {
                file.inputStream().use { input ->
                    val header = ByteArray(16)
                    val bytesRead = input.read(header)
                    validation.canReadContent = bytesRead > 0
                    
                    // Check for common video file signatures
                    val headerHex = header.take(8).joinToString(" ") { "%02x".format(it) }
                    android.util.Log.d("VideoValidation", "File header (hex): $headerHex")
                    
                    // MP4 signature check (ftyp)
                    val headerString = String(header, Charsets.ISO_8859_1)
                    validation.hasValidHeader = headerString.contains("ftyp") || 
                                              headerString.contains("moov") ||
                                              headerHex.startsWith("00 00 00") // Common MP4 start
                    
                    android.util.Log.d("VideoValidation", "Can read content: ${validation.canReadContent}")
                    android.util.Log.d("VideoValidation", "Has valid header: ${validation.hasValidHeader}")
                }
            } catch (e: Exception) {
                android.util.Log.e("VideoValidation", "Error reading file content", e)
                validation.contentError = e.message
            }
        }
        
        validation.isValid = validation.exists && 
                           validation.canRead && 
                           validation.size > 0 && 
                           validation.isFile &&
                           validation.hasVideoExtension
        
        android.util.Log.d("VideoValidation", "Overall valid: ${validation.isValid}")
        android.util.Log.d("VideoValidation", "=============================")
        
    } catch (e: Exception) {
        android.util.Log.e("VideoValidation", "Error during validation", e)
        validation.validationError = e.message
    }
    
    return validation
}

data class VideoFileValidation(
    var exists: Boolean = false,
    var canRead: Boolean = false,
    var size: Long = 0L,
    var isFile: Boolean = false,
    var parentExists: Boolean = false,
    var hasVideoExtension: Boolean = false,
    var canReadContent: Boolean = false,
    var hasValidHeader: Boolean = false,
    var isValid: Boolean = false,
    var contentError: String? = null,
    var validationError: String? = null
)

/**
 * ExoPlayer diagnostic information
 */
fun logExoPlayerDiagnostics(player: ExoPlayer?) {
    if (player == null) {
        android.util.Log.w("ExoPlayerDiag", "Player is null!")
        return
    }
    
    android.util.Log.d("ExoPlayerDiag", """
        |=== EXOPLAYER DIAGNOSTICS ===
        |Player state: ${getPlayerStateString(player.playbackState)}
        |Play when ready: ${player.playWhenReady}
        |Is playing: ${player.isPlaying}
        |Current position: ${player.currentPosition}ms
        |Duration: ${player.duration}ms
        |Buffered position: ${player.bufferedPosition}ms
        |Buffered percentage: ${player.bufferedPercentage}%
        |Current media item: ${player.currentMediaItem?.localConfiguration?.uri}
        |Media items count: ${player.mediaItemCount}
        |Current window index: ${player.currentMediaItemIndex}
        |Playback parameters: speed=${player.playbackParameters.speed}, pitch=${player.playbackParameters.pitch}
        |Audio attributes: ${player.audioAttributes}
        |Video size: ${player.videoSize.width}x${player.videoSize.height}
        |Is loading: ${player.isLoading}
        |==============================
    """.trimMargin())
    
    // Log track information
    val trackGroups = player.currentTracks
    android.util.Log.d("ExoPlayerDiag", "Available tracks:")
    for (i in 0 until trackGroups.groups.size) {
        val group = trackGroups.groups[i]
        android.util.Log.d("ExoPlayerDiag", "  Group $i: type=${group.type}, length=${group.length}")
        for (j in 0 until group.length) {
            val format = group.getTrackFormat(j)
            android.util.Log.d("ExoPlayerDiag", "    Track $j: ${format.sampleMimeType}, ${format.width}x${format.height}")
        }
    }
}

private fun getPlayerStateString(state: Int): String {
    return when (state) {
        Player.STATE_IDLE -> "IDLE"
        Player.STATE_BUFFERING -> "BUFFERING"
        Player.STATE_READY -> "READY"
        Player.STATE_ENDED -> "ENDED"
        else -> "UNKNOWN($state)"
    }
}

/**
 * Firebase Storage diagnostic
 */
suspend fun diagnoseFirebaseStorage(videoFileName: String) {
    try {
        android.util.Log.d("FirebaseDiag", "=== FIREBASE STORAGE DIAGNOSTICS ===")
        android.util.Log.d("FirebaseDiag", "Video filename: $videoFileName")
        
        val storage = FirebaseStorage.getInstance()
        val videoRef = storage.reference.child("VideosMessages/$videoFileName")
        
        android.util.Log.d("FirebaseDiag", "Storage reference: ${videoRef.path}")
        android.util.Log.d("FirebaseDiag", "Full path: ${videoRef.toString()}")
        
        // Try to get metadata
        try {
            val metadata = videoRef.metadata.await()
            android.util.Log.d("FirebaseDiag", """
                |File metadata:
                |  Name: ${metadata.name}
                |  Size: ${metadata.sizeBytes} bytes
                |  Content type: ${metadata.contentType}
                |  Created: ${metadata.creationTimeMillis}
                |  Updated: ${metadata.updatedTimeMillis}
                |  MD5: ${metadata.md5Hash}
            """.trimMargin())
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDiag", "Failed to get metadata", e)
        }
        
        // Try to get download URL
        try {
            val downloadUrl = videoRef.downloadUrl.await()
            android.util.Log.d("FirebaseDiag", "Download URL available: $downloadUrl")
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDiag", "Failed to get download URL", e)
        }
        
        android.util.Log.d("FirebaseDiag", "====================================")
        
    } catch (e: Exception) {
        android.util.Log.e("FirebaseDiag", "Firebase diagnostics failed", e)
    }
}

/**
 * System diagnostics for video playback
 */
fun logSystemDiagnostics(context: Context) {
    android.util.Log.d("SystemDiag", """
        |=== SYSTEM DIAGNOSTICS ===
        |Android version: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})
        |Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
        |Available memory: ${getAvailableMemory(context)}
        |Storage space: ${getAvailableStorage(context)}
        |Network connected: ${isNetworkConnected(context)}
        |===========================
    """.trimMargin())
}

private fun getAvailableMemory(context: Context): String {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    val memoryInfo = android.app.ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    return "${memoryInfo.availMem / (1024 * 1024)} MB"
}

private fun getAvailableStorage(context: Context): String {
    val stat = android.os.StatFs(context.filesDir.path)
    val availableBytes = stat.availableBytes
    return "${availableBytes / (1024 * 1024)} MB"
}

private fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
           networkCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
}

/**
 * Complete diagnostic function to call when video won't play
 */
suspend fun runCompleteVideoDiagnostics(
    context: Context,
    videoFileName: String,
    localVideoFile: File?,
    exoPlayer: ExoPlayer?
) {
    android.util.Log.i("VideoDiagnostics", "🔍 RUNNING COMPLETE VIDEO DIAGNOSTICS 🔍")
    
    // System diagnostics
    logSystemDiagnostics(context)
    
    // Firebase diagnostics
    diagnoseFirebaseStorage(videoFileName)
    
    // File validation
    localVideoFile?.let { file ->
        val validation = validateVideoFile(file)
        android.util.Log.i("VideoDiagnostics", "File validation result: $validation")
    } ?: android.util.Log.w("VideoDiagnostics", "No local video file to validate")
    
    // ExoPlayer diagnostics
    logExoPlayerDiagnostics(exoPlayer)
    
    android.util.Log.i("VideoDiagnostics", "🏁 DIAGNOSTICS COMPLETE 🏁")
}
