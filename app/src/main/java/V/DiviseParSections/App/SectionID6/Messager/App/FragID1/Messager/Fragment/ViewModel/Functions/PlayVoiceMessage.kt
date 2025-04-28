package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Functions

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

suspend fun ViewModelMessageur.playVoiceMessage(
    voiceMessageId: String?,
    context: Context,
    onPrepared: (MediaPlayer) -> Unit,
    onCompletion: () -> Unit,
    onError: () -> Unit,
): MediaPlayer? {  // Changed to return MediaPlayer
    if (voiceMessageId.isNullOrEmpty()) return null

    return withContext(Dispatchers.IO) {
        try {
            // Get download URL from Firebase Storage
            val storageRef = Firebase.storage.reference
                .child("1_messagesVocales")
                .child(voiceMessageId)

            val downloadUrl = withContext(Dispatchers.IO) {
                try {
                    storageRef.downloadUrl.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            if (downloadUrl != null) {
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
                        start()
                        onPrepared(this)
                    }
                    setOnCompletionListener {
                        onCompletion()
                    }
                    setOnErrorListener { _, _, _ ->
                        onError()
                        true
                    }
                    prepareAsync()
                }
                mediaPlayer  // Return the mediaPlayer instance
            } else {
                withContext(Dispatchers.Main) {
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
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                onError()
            }
            null
        }
    }
}
