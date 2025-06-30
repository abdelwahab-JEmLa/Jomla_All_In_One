package Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler

import V.DiviseParSections.App.Shared.Repository.GBonVent
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface AudioRecorderInterface {
    fun startRecording(
        context: Context,
        parentMessageVID: Long,
        currentTransaction: GBonVent? = null
    ): Result<AudioHandlerInterface.RecordingSession>

    fun stopRecording(): Result<File>
    fun forceCleanup()
}

interface AudioPlayerInterface {
    suspend fun startPlayback(
        context: Context,
        parentMessageVID: Long,
        firebaseUrl: String? = null,
        onPlaybackComplete: (() -> Unit)? = null,
        onPlaybackError: ((String) -> Unit)? = null
    ): Result<AudioHandlerInterface.PlaybackSession>

    fun stopPlayback(): Result<Unit>
    fun updatePlaybackProgress()
    fun getCurrentPlaybackSession(): AudioHandlerInterface.PlaybackSession?
    fun isPlaying(): Boolean
    val playbackProgress: StateFlow<AudioHandlerInterface.PlaybackProgress>
}

interface AudioFileManagerInterface {
    suspend fun uploadAudioFile(localFile: File, parentMessageVID: Long): Result<String>
    suspend fun downloadAudioFileIfNeeded(context: Context, parentMessageVID: Long): Result<File>
    suspend fun downloadAudioFileFromUrl(context: Context, firebaseUrl: String): Result<File>
}

interface AudioUtilsInterface {
    fun formatTime(seconds: Int): String
    fun formatTimeFromMillis(millis: Int): String
}

// Combined interface that the handler implements
interface AudioHandlerInterface : 
    AudioRecorderInterface,
    AudioPlayerInterface,
    AudioFileManagerInterface,
    AudioUtilsInterface {
    
    enum class RecordingState { IDLE, RECORDING, UPLOADING }
    enum class PlaybackState { IDLE, PLAYING, PAUSED, DOWNLOADING, ERROR }
    
    data class RecordingSession(
        val mediaRecorder: MediaRecorder,
        val outputFile: File,
        val parentMessageVID: Long,
        val currentTransaction: GBonVent? = null,
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
}
