package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.Functions
   /*
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
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

    return withContext(Dispatchers.IO) {
        try {
            // Append .aac extension if not already present
            val fileId = if (!voiceMessageId.endsWith(".aac")) "$voiceMessageId.aac" else voiceMessageId

            // Get download URL from Firebase Storage
            val storageRef = MessageVocale.storageRef
                .child(fileId)

            val downloadUrl = withContext(Dispatchers.IO) {
                try {
                    storageRef.downloadUrl.await()
                } catch (e: Exception) {
                    // Try an alternative approach with the raw ID
                    try {
                        MessageVocale.storageRef.child(voiceMessageId).downloadUrl.await()
                    } catch (e2: Exception) {
                        null
                    }
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
                    setOnErrorListener { _, what, extra ->
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
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                onError()
            }
            null
        }
    }
}
                               */
