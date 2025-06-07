package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.IOException

class AudioRecorderAndPlayHandler(
    private val firebaseAudioHelper: FirebaseAudioStorageHelper
) {

    // Recording states
    enum class RecordingState {
        IDLE, RECORDING, UPLOADING
    }

    // Playback states
    enum class PlaybackState {
        IDLE, PLAYING, PAUSED, DOWNLOADING, ERROR
    }

    data class RecordingSession(
        val mediaRecorder: MediaRecorder,
        val outputFile: File,
        val parentMessageVID: Long,
        val currentTransaction: C3_BonAchate? = null,
        val state: RecordingState = RecordingState.RECORDING
    )

    data class PlaybackSession(
        val mediaPlayer: MediaPlayer,
        val audioFile: File,
        val parentMessageVID: Long,
        val state: PlaybackState = PlaybackState.PLAYING,
        val duration: Int = 0,
        val currentPosition: Int = 0
    )

    data class PlaybackProgress(
        val currentPosition: Int = 0,
        val duration: Int = 0,
        val progress: Float = 0f,
        val isPlaying: Boolean = false,
        val isDownloading: Boolean = false
    )

    private var currentRecordingSession: RecordingSession? = null
    private var currentPlaybackSession: PlaybackSession? = null

    // Playback progress state flow
    private val _playbackProgress = MutableStateFlow(PlaybackProgress())
    val playbackProgress: StateFlow<PlaybackProgress> = _playbackProgress.asStateFlow()

    // Recording functions (existing)
    fun startRecording(
        context: Context,
        parentMessageVID: Long,
        currentTransaction: C3_BonAchate? = null
    ): Result<RecordingSession> {
        return try {
            // Ensure no recording is in progress
            if (currentRecordingSession != null) {
                return Result.failure(IllegalStateException("Recording already in progress"))
            }

            val outputFile = File(context.filesDir, "voice_${parentMessageVID}.3gp")

            val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(outputFile.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                try {
                    prepare()
                    start()
                } catch (e: IOException) {
                    release()
                    throw IOException("Failed to start recording: ${e.message}")
                }
            }

            val session = RecordingSession(
                mediaRecorder = mediaRecorder,
                outputFile = outputFile,
                parentMessageVID = parentMessageVID,
                currentTransaction = currentTransaction
            )

            currentRecordingSession = session
            Result.success(session)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun stopRecording(): Result<File> {
        return try {
            val session = currentRecordingSession
                ?: return Result.failure(IllegalStateException("No recording session in progress"))

            session.mediaRecorder.apply {
                stop()
                release()
            }

            val file = session.outputFile

            // Verify file was created successfully
            if (!file.exists() || file.length() == 0L) {
                return Result.failure(IOException("Recording failed - file is empty or doesn't exist"))
            }

            // Clear current session
            currentRecordingSession = null

            Result.success(file)

        } catch (e: Exception) {
            // Clean up on error
            currentRecordingSession?.mediaRecorder?.apply {
                try {
                    release()
                } catch (ex: Exception) {
                    // Ignore cleanup errors
                }
            }
            currentRecordingSession = null

            Result.failure(Exception("Failed to stop recording: ${e.message}"))
        }
    }

    // New Playback functions
    suspend fun startPlayback(
        context: Context,
        parentMessageVID: Long,
        onPlaybackComplete: (() -> Unit)? = null,
        onPlaybackError: ((String) -> Unit)? = null
    ): Result<PlaybackSession> {
        return try {
            // Stop any current playback
            stopPlayback()

            // Update progress to show downloading
            _playbackProgress.value = _playbackProgress.value.copy(
                isDownloading = true,
                isPlaying = false
            )

            // Download audio file if needed
            val downloadResult = downloadAudioFileIfNeeded(context, parentMessageVID)

            _playbackProgress.value = _playbackProgress.value.copy(isDownloading = false)

            if (downloadResult.isFailure) {
                onPlaybackError?.invoke("Failed to download audio: ${downloadResult.exceptionOrNull()?.message}")
                return Result.failure(downloadResult.exceptionOrNull() ?: Exception("Download failed"))
            }

            val audioFile = downloadResult.getOrThrow()

            // Verify file exists and is not empty
            if (!audioFile.exists() || audioFile.length() == 0L) {
                onPlaybackError?.invoke("Audio file is empty or doesn't exist")
                return Result.failure(IOException("Audio file is empty or doesn't exist"))
            }

            // Create and configure MediaPlayer
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()

                setOnCompletionListener {
                    // Reset progress
                    _playbackProgress.value = PlaybackProgress()

                    // Clean up session
                    currentPlaybackSession = null

                    // Call completion callback
                    onPlaybackComplete?.invoke()

                    // Release resources
                    release()
                }

                setOnErrorListener { _, what, extra ->
                    val errorMessage = "MediaPlayer error: what=$what, extra=$extra"
                    onPlaybackError?.invoke(errorMessage)

                    // Reset progress
                    _playbackProgress.value = PlaybackProgress()

                    // Clean up session
                    currentPlaybackSession = null

                    true // Indicate we handled the error
                }

                start()
            }

            val session = PlaybackSession(
                mediaPlayer = mediaPlayer,
                audioFile = audioFile,
                parentMessageVID = parentMessageVID,
                duration = mediaPlayer.duration,
                currentPosition = 0
            )

            currentPlaybackSession = session

            // Update initial progress
            _playbackProgress.value = PlaybackProgress(
                duration = mediaPlayer.duration,
                currentPosition = 0,
                progress = 0f,
                isPlaying = true,
                isDownloading = false
            )

            Result.success(session)

        } catch (e: Exception) {
            _playbackProgress.value = PlaybackProgress()
            onPlaybackError?.invoke("Failed to start playback: ${e.message}")
            Result.failure(e)
        }
    }

    fun stopPlayback(): Result<Unit> {
        return try {
            currentPlaybackSession?.mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }

            currentPlaybackSession = null
            _playbackProgress.value = PlaybackProgress()

            Result.success(Unit)
        } catch (e: Exception) {
            currentPlaybackSession = null
            _playbackProgress.value = PlaybackProgress()
            Result.failure(e)
        }
    }


    fun updatePlaybackProgress() {
        currentPlaybackSession?.let { session ->
            try {
                if (session.mediaPlayer.isPlaying) {
                    val currentPosition = session.mediaPlayer.currentPosition
                    val duration = session.mediaPlayer.duration
                    val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f

                    _playbackProgress.value = _playbackProgress.value.copy(
                        currentPosition = currentPosition,
                        duration = duration,
                        progress = progress,
                        isPlaying = true
                    )
                }
            } catch (e: Exception) {
                // Handle error silently or log if needed
            }
        }
    }

    // File management functions (existing)
    suspend fun uploadAudioFile(localFile: File, parentMessageVID: Long): Result<String> {
        return firebaseAudioHelper.uploadAudioFile(localFile, parentMessageVID)
    }

    suspend fun downloadAudioFileIfNeeded(
        context: Context,
        parentMessageVID: Long
    ): Result<File> {
        return try {
            val audioFile = File(context.filesDir, "voice_${parentMessageVID}.3gp")

            // Check if file exists locally
            if (audioFile.exists() && audioFile.length() > 0) {
                // File already exists locally, return it
                Result.success(audioFile)
            } else {
                // File doesn't exist locally, download from Firebase
                firebaseAudioHelper.downloadAudioFile(context, parentMessageVID)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get audio file: ${e.message}"))
        }
    }

    // State query functions
    fun getCurrentPlaybackSession(): PlaybackSession? = currentPlaybackSession
    fun isPlaying(): Boolean = currentPlaybackSession?.mediaPlayer?.isPlaying == true

    // Cleanup functions
    fun forceCleanup() {
        // Clean up recording
        currentRecordingSession?.mediaRecorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
        currentRecordingSession = null

        // Clean up playback
        currentPlaybackSession?.mediaPlayer?.apply {
            try {
                if (isPlaying) {
                    stop()
                }
                release()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
        currentPlaybackSession = null

        _playbackProgress.value = PlaybackProgress()
    }

    // Utility functions
    @SuppressLint("DefaultLocale")
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    @SuppressLint("DefaultLocale")
    fun formatTimeFromMillis(millis: Int): String {
        val seconds = millis / 1000
        return formatTime(seconds)
    }

    // Deprecated functions
    @Deprecated("Use stopRecording() instead")
    fun stopRecording(mediaRecorder: MediaRecorder?) {
        stopRecording()
    }
}
