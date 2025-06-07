package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Module

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class AudioRecorderAndPlayHandler(val firebaseAudioHelper: FirebaseAudioStorageHelper
) {
    @SuppressLint("DefaultLocale")
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun startRecording(
        context: Context,
        parentMessageVID: Long
    ): Pair<MediaRecorder, File> {
        val outputFile = File(context.filesDir, "voice_${parentMessageVID}.3gp")

        val mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(outputFile.absolutePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                throw IOException("Failed to start recording: ${e.message}")
            }
        }

        return Pair(mediaRecorder, outputFile)
    }

    fun stopRecording(mediaRecorder: MediaRecorder?): Unit {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            throw Exception("Failed to stop recording: ${e.message}")
        }
    }

    /**
     * Downloads audio file from Firebase if it doesn't exist locally
     * @param context The Android context
     * @param parentMessageVID The unique message ID
     * @return Result containing the local file or an exception
     */
    suspend fun downloadAudioFileIfNeeded(
        context: Context,
        parentMessageVID: Long
    ): Result<File> {
        return try {
            val audioFileKey = "voice_${parentMessageVID}"
            val audioFile = File(context.filesDir, "$audioFileKey.3gp")

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
     * Uploads audio file to Firebase Storage
     * @param localFile The local audio file to upload
     * @param parentMessageVID The unique message ID
     * @return Result containing the download URL or an exception
     */
    suspend fun uploadAudioFile(
        localFile: File,
        parentMessageVID: Long
    ): Result<String> {
        return firebaseAudioHelper.uploadAudioFile(localFile, parentMessageVID)
    }
}
