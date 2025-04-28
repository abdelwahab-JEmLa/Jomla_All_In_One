package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.Functions

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
private const val TAG = "MessageVocaleRecorder"

suspend fun ViewModelMessageur.playVoiceMessage(
    voiceMessageId: String?,
    context: Context,
    onPrepared: (MediaPlayer) -> Unit,
    onCompletion: () -> Unit,
    onError: () -> Unit,
): MediaPlayer? {
    if (voiceMessageId.isNullOrEmpty()) {
        Log.e(TAG, "Empty voice message ID provided")
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                "ID de message vocal vide",
                Toast.LENGTH_SHORT
            ).show()
            onError()
        }
        return null
    }

    Log.d(TAG, "Attempting to play voice message with ID: $voiceMessageId")

    return withContext(Dispatchers.IO) {
        try {
            // Append .aac extension if not already present
            val fileId = if (!voiceMessageId.endsWith(".aac")) "$voiceMessageId.aac" else voiceMessageId
            Log.d(TAG, "Using file ID for playback: $fileId")

            // Get download URL from Firebase Storage
            val storageRef = MessageVocale.storageRef
                .child(fileId)

            Log.d(TAG, "Attempting to get download URL from Firebase for: $fileId")
            val downloadUrl = withContext(Dispatchers.IO) {
                try {
                    storageRef.downloadUrl.await()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to get download URL: ${e.message}", e)
                    // Try an alternative approach with the raw ID
                    try {
                        Log.d(TAG, "Trying alternative approach with raw ID")
                        MessageVocale.storageRef.child(voiceMessageId).downloadUrl.await()
                    } catch (e2: Exception) {
                        Log.e(TAG, "Failed alternative approach: ${e2.message}", e2)
                        null
                    }
                }
            }

            if (downloadUrl != null) {
                Log.d(TAG, "Got download URL: $downloadUrl")
                // Create and prepare MediaPlayer
                val mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(downloadUrl.toString())
                    setOnPreparedListener {
                        Log.d(TAG, "MediaPlayer prepared, starting playback")
                        start()
                        onPrepared(this)
                    }
                    setOnCompletionListener {
                        Log.d(TAG, "Playback completed")
                        onCompletion()
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "MediaPlayer error during playback: what=$what, extra=$extra")
                        onError()
                        true
                    }
                    prepareAsync()
                }
                mediaPlayer  // Return the mediaPlayer instance
            } else {
                withContext(Dispatchers.Main) {
                    Log.e(TAG, "Failed to get download URL for voice message")
                    Toast.makeText(
                        context,
                        "Impossible de charger le message vocal",
                        Toast.LENGTH_SHORT
                    ).show()
                    onError()
                }
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing voice message: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                onError()
            }
            null
        }
    }
}
