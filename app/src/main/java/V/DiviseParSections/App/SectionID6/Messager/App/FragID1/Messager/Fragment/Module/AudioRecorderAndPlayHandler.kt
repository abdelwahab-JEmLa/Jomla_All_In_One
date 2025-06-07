package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorderAndPlayHandler(
    private val firebaseAudioHelper: FirebaseAudioStorageHelper
) {

    // Recording states
    enum class RecordingState {
        IDLE, RECORDING, UPLOADING
    }

    data class RecordingSession(
        val mediaRecorder: MediaRecorder,
        val outputFile: File,
        val parentMessageVID: Long,
        val currentTransaction: C3_BonAchate? = null,
        val state: RecordingState = RecordingState.RECORDING
    )

    private var currentSession: RecordingSession? = null

    /**
     * Start recording with optional transaction parameter
     * This method works for both ButtonMessageVocale and ButtonAjouteHistoriqueC3_BonAchate
     */
    fun startRecording(
        context: Context,
        parentMessageVID: Long,
        currentTransaction: C3_BonAchate? = null
    ): Result<RecordingSession> {
        return try {
            // Ensure no recording is in progress
            if (currentSession != null) {
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

            currentSession = session
            Result.success(session)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Stop recording - works for both components
     */
    fun stopRecording(): Result<File> {
        return try {
            val session = currentSession
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
            currentSession = null

            Result.success(file)

        } catch (e: Exception) {
            // Clean up on error
            currentSession?.mediaRecorder?.apply {
                try {
                    release()
                } catch (ex: Exception) {
                    // Ignore cleanup errors
                }
            }
            currentSession = null

            Result.failure(Exception("Failed to stop recording: ${e.message}"))
        }
    }

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

    /**
     * Get current recording session info
     */
    fun getCurrentSession(): RecordingSession? = currentSession

    /**
     * Get current transaction from the recording session
     */
    fun getCurrentTransaction(): C3_BonAchate? = currentSession?.currentTransaction

    /**
     * Check if currently recording
     */
    fun isRecording(): Boolean = currentSession != null

    /**
     * Force cleanup of current session (emergency stop)
     */
    fun forceCleanup() {
        currentSession?.mediaRecorder?.apply {
            try {
                stop()
                release()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
        currentSession = null
    }

    /**
     * Format time in MM:SS format
     */
    @SuppressLint("DefaultLocale")
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    @Deprecated("Use stopRecording() instead")
    fun stopRecording(mediaRecorder: MediaRecorder?) {
        stopRecording()
    }
}
