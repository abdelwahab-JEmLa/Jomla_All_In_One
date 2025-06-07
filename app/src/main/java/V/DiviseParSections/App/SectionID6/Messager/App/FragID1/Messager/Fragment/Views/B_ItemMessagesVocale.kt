package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    parentD_EtateMessageVocale: D_EtateMessageVocale,
    etatesChildKeyIDsList: List<D_EtateMessageVocale>,
    viewModel: ViewModelMessageur,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }
    val audioHandler = viewModel.audioRecorderAndPlayHandler

    // Collect playback progress from the handler
    val playbackProgress by audioHandler.playbackProgress.collectAsState()

    // Check message states
    val isListened = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.ECOUTE }
    val isViewed = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.VUE }
    val isBeingRecorded = etatesChildKeyIDsList.any {
        it.nom == D_EtateMessageVocale.Nom.EN_COURT_ENREGESTREMENT
    }
    val isSent = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.ENVOYER }

    // Get the latest state timestamp
    val latestTimestamp = etatesChildKeyIDsList.maxByOrNull { it.timestamps }?.timestamps ?: 0L

    // Check if this specific message is currently playing - FIXED: More defensive checks
    val isCurrentlyPlaying = remember(playbackProgress.isPlaying, audioHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioHandler.getCurrentPlaybackSession()?.parentMessageVID == parentD_EtateMessageVocale.parentMessageVID && playbackProgress.isPlaying
    }

    // Check if this message is currently downloading - FIXED: More defensive checks
    val isCurrentlyDownloading = remember(playbackProgress.isDownloading, audioHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioHandler.getCurrentPlaybackSession()?.parentMessageVID == parentD_EtateMessageVocale.parentMessageVID && playbackProgress.isDownloading
    }

    // Update progress periodically while playing - FIXED: Prevent memory leaks
    LaunchedEffect(parentD_EtateMessageVocale.parentMessageVID, isCurrentlyPlaying) {
        if (isCurrentlyPlaying) {
            try {
                while (isCurrentlyPlaying && audioHandler.isPlaying()) {
                    audioHandler.updatePlaybackProgress()
                    delay(100)

                    // Safety check: break if session is no longer for this message
                    if (audioHandler.getCurrentPlaybackSession()?.parentMessageVID != parentD_EtateMessageVocale.parentMessageVID) {
                        break
                    }
                }
            } catch (e: Exception) {
                // Handle any exceptions to prevent crashes
                e.printStackTrace()
            }
        }
    }

    // Cleanup when leaving composition - FIXED: More aggressive cleanup
    DisposableEffect(parentD_EtateMessageVocale.parentMessageVID) {
        onDispose {
            try {
                // Only cleanup if this item is currently playing
                val currentSession = audioHandler.getCurrentPlaybackSession()
                if (currentSession?.parentMessageVID == parentD_EtateMessageVocale.parentMessageVID) {
                    audioHandler.stopPlayback()
                }
            } catch (e: Exception) {
                // Ignore cleanup errors to prevent crashes
                e.printStackTrace()
            }
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
            // Message header with message info and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Message vocal #${parentD_EtateMessageVocale.parentMessageVID}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "الوقت: ${datesHandler.getDateAndTimString(parentD_EtateMessageVocale.timestamps).time}",
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
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else if (isSent) {
                // Audio player controls - only show for sent messages
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play/Pause/Stop Button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                when {
                                    isCurrentlyPlaying -> {
                                        // Stop current playback
                                        val stopResult = audioHandler.stopPlayback()
                                        if (stopResult.isFailure) {
                                            Toast.makeText(
                                                context,
                                                "Erreur lors de l'arrêt: ${stopResult.exceptionOrNull()?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    else -> {
                                        // Start playback
                                        val playResult = audioHandler.startPlayback(
                                            context = context,
                                            parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                                            onPlaybackComplete = {
                                                // Update message state to ECOUTE if not already
                                                if (!isListened) {
                                                    coroutineScope.launch {
                                                        try {
                                                            val newEtate = D_EtateMessageVocale(
                                                                parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                                                                nom = D_EtateMessageVocale.Nom.ECOUTE,
                                                                timestamps = datesHandler.getCurrentTimestamps()
                                                            )
                                                            viewModel.addOrUpdateData(newEtate)
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                    }
                                                }
                                            },
                                            onPlaybackError = { errorMessage ->
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        )

                                        if (playResult.isFailure) {
                                            Toast.makeText(
                                                context,
                                                "Erreur lors du démarrage: ${playResult.exceptionOrNull()?.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !isCurrentlyDownloading // Disable button while downloading
                    ) {
                        Icon(
                            imageVector = when {
                                isCurrentlyDownloading -> Icons.Default.PlayArrow // Show play icon while downloading
                                isCurrentlyPlaying -> Icons.Default.Stop
                                else -> Icons.Default.PlayArrow
                            },
                            contentDescription = when {
                                isCurrentlyDownloading -> "Téléchargement en cours"
                                isCurrentlyPlaying -> "Arrêter la lecture"
                                else -> "Lecture du message vocal"
                            },
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    // Progress Bar - FIXED: Use proper conditional logic
                    when {
                        isCurrentlyDownloading -> {
                            // Show indeterminate progress while downloading
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .padding(horizontal = 8.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            )
                        }
                        isCurrentlyPlaying && playbackProgress.duration > 0 -> {
                            // Show determinate progress while playing
                            LinearProgressIndicator(
                                progress = { playbackProgress.progress.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .padding(horizontal = 8.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        }
                        else -> {
                            // Show empty progress bar when not playing
                            LinearProgressIndicator(
                                progress = { 0f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .padding(horizontal = 8.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        }
                    }

                    // Time display and status
                    Column(
                        modifier = Modifier.padding(start = 4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        when {
                            isCurrentlyDownloading -> {
                                Text(
                                    text = "Téléchargement...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            isCurrentlyPlaying && playbackProgress.duration > 0 -> {
                                // Show current time / total time
                                Text(
                                    text = "${audioHandler.formatTimeFromMillis(playbackProgress.currentPosition)} / ${audioHandler.formatTimeFromMillis(playbackProgress.duration)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            isListened -> {
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
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                            else -> {
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
            } else {
                // Message is in an unknown state
                Text(
                    text = "État du message inconnu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
