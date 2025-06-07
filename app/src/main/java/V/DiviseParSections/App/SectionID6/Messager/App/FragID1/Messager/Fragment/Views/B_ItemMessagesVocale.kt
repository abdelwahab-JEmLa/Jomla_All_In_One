package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views
     /*
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.MessageurUiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.Functions.playVoiceMessage
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun B_ItemMessagesVocale(
    parentMessageVocale: MessageVocale,
    etatesChildKeyIDsList: List<D_EtateMessageVocale>,
    uiState: MessageurUiState,
    viewModel: ViewModelMessageur,
) {
    // For audio playback
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val datesHandler = remember { DatesHandler() }

    // Check message states
    val isListened = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.ECOUTE }
    val isViewed = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.VUE }
    val isBeingRecorded = etatesChildKeyIDsList.any {
        it.nom == D_EtateMessageVocale.Nom.EN_COURT_ENREGESTREMENT
    }
    val isSent = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.ENVOYER }

    // Get the latest state timestamp
    val latestTimestamp = etatesChildKeyIDsList.maxByOrNull { it.timestamps }?.timestamps ?: 0L

    // Cleanup MediaPlayer when leaving composition
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isListened -> MaterialTheme.colorScheme.primaryContainer
                isViewed -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Message header with client name and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = try {
                        when {
                            parentMessageVocale.vocaleKeyID.startsWith("voice_") -> {
                                // Extract the message ID part
                                val parts = parentMessageVocale.vocaleKeyID.split("_")
                                if (parts.size >= 2) {
                                    "Message vocal #${parts[1]}"
                                } else {
                                    "Message vocal"
                                }
                            }
                            parentMessageVocale.vocaleKeyID.isNotEmpty() -> {
                                "Message: ${parentMessageVocale.vocaleKeyID}"
                            }
                            else -> {
                                "Message vocal"
                            }
                        }
                    } catch (e: Exception) {
                        "Message vocal" // Error fallback
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "الوقت: ${
                        datesHandler.getDateAndTimString(
                            parentMessageVocale.vocaleKeyID.hashCode().toLong()
                        ).time
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Show different UI based on message state
            if (isBeingRecorded && !isSent) {
                // Display "Recording in progress" text instead of player controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enregistrement en cours...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                // Audio player controls - only show for sent messages
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play/Stop Button
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                // Stop playing
                                mediaPlayer?.apply {
                                    stop()
                                    release()
                                }
                                mediaPlayer = null
                                isPlaying = false
                                playbackProgress = 0f
                            } else {
                                // Start playing
                                coroutineScope.launch {
                                    viewModel.playVoiceMessage(
                                        parentMessageVocale.vocaleKeyID,
                                        context,
                                        onPrepared = { player ->
                                            mediaPlayer = player
                                            isPlaying = true

                                            // Update progress while playing
                                            coroutineScope.launch {
                                                while (isPlaying && mediaPlayer != null) {
                                                    val duration = mediaPlayer?.duration ?: 1
                                                    val currentPosition =
                                                        mediaPlayer?.currentPosition ?: 0
                                                    playbackProgress =
                                                        currentPosition.toFloat() / duration.toFloat()
                                                    delay(100)
                                                }
                                            }
                                        },
                                        onCompletion = {
                                            isPlaying = false
                                            playbackProgress = 0f
                                            mediaPlayer?.release()
                                            mediaPlayer = null

                                            // Update message state to ECOUTE if not already
                                            if (!isListened) {
                                                coroutineScope.launch {
                                                    // Create a new D_EtateMessageVocale with ECOUTE state
                                                    val newEtate = D_EtateMessageVocale(
                                                        parentMessageVID = parentMessageVocale.vid,
                                                        parentMessageKeyID = parentMessageVocale.keyID,
                                                        nom = D_EtateMessageVocale.Nom.ECOUTE,
                                                        timestamps = datesHandler.getCurrentTimestamps()
                                                    )
                                                    viewModel.appDatabase.D_EtateMessageVocaleDao()
                                                        .insert(newEtate)
                                                }
                                            }
                                        },
                                        onError = {
                                            isPlaying = false
                                            playbackProgress = 0f
                                            Toast.makeText(
                                                context,
                                                "Erreur de lecture du message vocal",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Arrêter la lecture" else "Lecture du message vocal",
                            tint = Color.White
                        )
                    }

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { playbackProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    // Status icon
                    Column(
                        modifier = Modifier.padding(start = 4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (isListened) {
                            // Show check mark if message has been listened to
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Message écouté",
                                tint = Color.Green,
                                modifier = Modifier.size(24.dp)
                            )
                            // Show when the message was listened to
                            if (latestTimestamp > 0) {
                                Text(
                                    text = datesHandler.getDateAndTimString(latestTimestamp).time,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        } else {
                            // Show warning if message has not been listened to
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Message non écouté",
                                tint = Color.Yellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
                   */
