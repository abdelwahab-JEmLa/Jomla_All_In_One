package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Functions.playVoiceMessage
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
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
fun MessagesVocaleItem(
    messageDetails: MessageVocale,
    etates: List<EtateMessageVocale>,
    viewModel: ViewModelMessageur
) {
    // For audio playback
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val datesHandler = remember { DatesHandler() }

    // Check if message has been listened to
    val isListened = etates.any { it.nom == EtateMessageVocale.Nom.ECOUTE }
    val isViewed = etates.any { it.nom == EtateMessageVocale.Nom.VUE }

    // Get the latest state timestamp
    val latestTimestamp = etates.maxByOrNull { it.timestamps }?.timestamps ?: 0L

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
                    text = messageDetails.nomClientConcerned,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "الوقت: ${
                        datesHandler.getDateAndTimString(
                            messageDetails.vocaleKeyID.hashCode().toLong()
                        ).time
                    }",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Audio player controls
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
                                stop() // Fixed: Removed redundant isPlaying check
                                release()
                            }
                            mediaPlayer = null
                            isPlaying = false
                            playbackProgress = 0f
                        } else {
                            // Start playing
                            coroutineScope.launch {
                                // Fixed: Store and use the player variable
                                viewModel.playVoiceMessage(
                                    messageDetails.vocaleKeyID,
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
                                                // Create a new EtateMessageVocale with ECOUTE state
                                                val newEtate = EtateMessageVocale(
                                                    parentMessageVID = messageDetails.vid,
                                                    parentMessageKeyID = messageDetails.fireBaseKeyID,
                                                    nom = EtateMessageVocale.Nom.ECOUTE,
                                                    timestamps = datesHandler.getCurrentTimestamps()
                                                )
                                                viewModel.appDatabase.etateMessageVocaleDao()
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

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
