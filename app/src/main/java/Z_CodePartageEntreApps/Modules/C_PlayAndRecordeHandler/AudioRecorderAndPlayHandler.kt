package Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.TransactionCommercial
import Z_CodePartageEntreApps.Modules.A_FirebaseAudioStorageHelper
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
    private val firebaseAudioHelper: A_FirebaseAudioStorageHelper
) : AudioHandlerInterface {

    private var currentRecordingSession: AudioHandlerInterface.RecordingSession? = null
    private var currentPlaybackSession: AudioHandlerInterface.PlaybackSession? = null

    private val _playbackProgress = MutableStateFlow(AudioHandlerInterface.PlaybackProgress())
    override val playbackProgress: StateFlow<AudioHandlerInterface.PlaybackProgress> = _playbackProgress.asStateFlow()

    override fun startRecording(
        context: Context,
        parentMessageVID: Long,
        currentTransaction: TransactionCommercial?
    ): Result<AudioHandlerInterface.RecordingSession> {
        return try {
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

            var tempSession: AudioHandlerInterface.RecordingSession? = null

            try {
                mediaRecorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(outputFile.absolutePath)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    prepare()
                }

                tempSession = AudioHandlerInterface.RecordingSession(
                    mediaRecorder = mediaRecorder,
                    outputFile = outputFile,
                    parentMessageVID = parentMessageVID,
                    currentTransaction = currentTransaction
                )

                mediaRecorder.start()
                currentRecordingSession = tempSession
                Result.success(tempSession)

            } catch (e: IOException) {
                try { mediaRecorder.release() } catch (_: Exception) { }
                if (outputFile.exists()) {
                    try { outputFile.delete() } catch (_: Exception) { }
                }
                throw IOException("Failed to start recording: ${e.message}", e)
            }

        } catch (e: Exception) {
            currentRecordingSession = null
            Result.failure(e)
        }
    }

    override fun stopRecording(): Result<File> {
        return try {
            val session = currentRecordingSession
                ?: return Result.failure(IllegalStateException("No recording session in progress"))

            try {
                session.mediaRecorder.apply { stop(); release() }
            } catch (e: Exception) {
                try { session.mediaRecorder.release() } catch (_: Exception) { }
            }

            val file = session.outputFile
            currentRecordingSession = null

            if (!file.exists() || file.length() == 0L) {
                if (file.exists()) {
                    try { file.delete() } catch (_: Exception) { }
                }
                return Result.failure(IOException("Recording failed - file is empty or doesn't exist"))
            }

            Result.success(file)

        } catch (e: Exception) {
            currentRecordingSession?.let { session ->
                try { session.mediaRecorder.release() } catch (_: Exception) { }
                if (session.outputFile.exists()) {
                    try { session.outputFile.delete() } catch (_: Exception) { }
                }
            }
            currentRecordingSession = null
            Result.failure(Exception("Failed to stop recording: ${e.message}", e))
        }
    }

    override suspend fun startPlayback(
        context: Context,
        parentMessageVID: Long,
        firebaseUrl: String?,
        onPlaybackComplete: (() -> Unit)?,
        onPlaybackError: ((String) -> Unit)?
    ): Result<AudioHandlerInterface.PlaybackSession> {
        return try {
            stopPlayback()

            _playbackProgress.value = _playbackProgress.value.copy(
                isDownloading = true,
                isPlaying = false
            )

            val downloadResult = if (firebaseUrl != null && firebaseUrl.isNotEmpty()) {
                downloadAudioFileFromUrl(context, firebaseUrl)
            } else {
                downloadAudioFileIfNeeded(context, parentMessageVID)
            }

            _playbackProgress.value = _playbackProgress.value.copy(isDownloading = false)

            if (downloadResult.isFailure) {
                onPlaybackError?.invoke("Failed to download audio: ${downloadResult.exceptionOrNull()?.message}")
                return Result.failure(downloadResult.exceptionOrNull() ?: Exception("Download failed"))
            }

            val audioFile = downloadResult.getOrThrow()

            if (!audioFile.exists() || audioFile.length() == 0L) {
                onPlaybackError?.invoke("Audio file is empty or doesn't exist")
                return Result.failure(IOException("Audio file is empty or doesn't exist"))
            }

            val mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()

                setOnCompletionListener {
                    _playbackProgress.value = AudioHandlerInterface.PlaybackProgress()
                    currentPlaybackSession = null
                    onPlaybackComplete?.invoke()
                    release()
                }

                setOnErrorListener { _, what, extra ->
                    val errorMessage = "MediaPlayer error: what=$what, extra=$extra"
                    onPlaybackError?.invoke(errorMessage)
                    _playbackProgress.value = AudioHandlerInterface.PlaybackProgress()
                    currentPlaybackSession = null
                    true
                }

                start()
            }

            val session = AudioHandlerInterface.PlaybackSession(
                mediaPlayer = mediaPlayer,
                audioFile = audioFile,
                parentMessageVID = parentMessageVID,
                duration = mediaPlayer.duration,
                currentPosition = 0
            )

            currentPlaybackSession = session

            _playbackProgress.value = AudioHandlerInterface.PlaybackProgress(
                duration = mediaPlayer.duration,
                currentPosition = 0,
                progress = 0f,
                isPlaying = true,
                isDownloading = false
            )

            Result.success(session)

        } catch (e: Exception) {
            _playbackProgress.value = AudioHandlerInterface.PlaybackProgress()
            onPlaybackError?.invoke("Failed to start playback: ${e.message}")
            Result.failure(e)
        }
    }

    override fun stopPlayback(): Result<Unit> {
        return try {
            currentPlaybackSession?.mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            currentPlaybackSession = null
            _playbackProgress.value = AudioHandlerInterface.PlaybackProgress()
            Result.success(Unit)
        } catch (e: Exception) {
            currentPlaybackSession = null
            _playbackProgress.value = AudioHandlerInterface.PlaybackProgress()
            Result.failure(e)
        }
    }

    override fun updatePlaybackProgress() {
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
            } catch (_: Exception) { }
        }
    }

    override suspend fun uploadAudioFile(localFile: File, parentMessageVID: Long): Result<String> {
        return firebaseAudioHelper.uploadAudioFile(localFile, parentMessageVID)
    }

    override suspend fun downloadAudioFileIfNeeded(
        context: Context,
        parentMessageVID: Long
    ): Result<File> {
        return try {
            val audioFile = File(context.filesDir, "voice_${parentMessageVID}.3gp")
            if (audioFile.exists() && audioFile.length() > 0) {
                Result.success(audioFile)
            } else {
                firebaseAudioHelper.downloadAudioFile(context, parentMessageVID)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get audio file: ${e.message}"))
        }
    }

    override suspend fun downloadAudioFileFromUrl(
        context: Context,
        firebaseUrl: String
    ): Result<File> {
        return try {
            firebaseAudioHelper.downloadAudioFileFromUrl(context, firebaseUrl)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to download audio file from URL: ${e.message}"))
        }
    }

    override fun getCurrentPlaybackSession(): AudioHandlerInterface.PlaybackSession? = currentPlaybackSession
    override fun isPlaying(): Boolean = currentPlaybackSession?.mediaPlayer?.isPlaying == true

    override fun forceCleanup() {
        currentRecordingSession?.mediaRecorder?.apply {
            try { stop(); release() } catch (_: Exception) { }
        }
        currentRecordingSession = null

        currentPlaybackSession?.mediaPlayer?.apply {
            try { if (isPlaying) stop(); release() } catch (_: Exception) { }
        }
        currentPlaybackSession = null

        _playbackProgress.value = AudioHandlerInterface.PlaybackProgress()
    }

    @SuppressLint("DefaultLocale")
    override fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    @SuppressLint("DefaultLocale")
    override fun formatTimeFromMillis(millis: Int): String {
        val seconds = millis / 1000
        return formatTime(seconds)
    }

    @Deprecated("Use stopRecording() instead")
    fun stopRecording(mediaRecorder: MediaRecorder?) {
        stopRecording()
    }
}
