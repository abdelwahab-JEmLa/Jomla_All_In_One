package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Module

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AudioRecorderAndPlayHandler(val firebaseAudioHelper: FirebaseAudioStorageHelper
) {

    fun startRecording(context: Context): Pair<MediaRecorder, File> {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "voice_$timestamp.aac"  // Utiliser AAC au lieu de 3GP
        val file = File(context.cacheDir, fileName)

        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            // Configuration pour AAC (qualité faible pour petit fichier)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(1)  // Mono
            setAudioSamplingRate(16000)  // 16kHz - bon pour la voix
            setAudioEncodingBitRate(32000)  // 32kbps - taille réduite mais qualité suffisante pour la voix
            setOutputFile(file.absolutePath)

            try {
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Pair(recorder, file)
    }

    fun stopRecording(
        recorder: MediaRecorder?,
        context: Context,
        file: File?,
        onComplete: (File) -> Unit
    ) {
        try {
            recorder?.apply {
                stop()
                release()
            }
            file?.let { onComplete(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "Erreur lors de l'arrêt de l'enregistrement",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun uploadVoiceMessage(
        file: File?,
        clientId: Long?,
        context: Context,
        onSuccess: (String) -> Unit
    ) {
        if (file == null) return

        val messagesVocalesRef = Firebase.storage.reference
            .child("1_messagesVocales")

        // Generate a unique filename for the voice message
        val fileId = "voice_${clientId}_${UUID.randomUUID()}.aac"  // Extension AAC
        val fileRef = messagesVocalesRef.child(fileId)

        fileRef.putFile(android.net.Uri.fromFile(file))
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Message vocal enregistré avec succès",
                    Toast.LENGTH_SHORT
                ).show()
                onSuccess(fileId)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Échec de l'enregistrement du message: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

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
