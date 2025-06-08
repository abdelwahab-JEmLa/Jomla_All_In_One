package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Module.PlayeAndRecordeHandler.AudioHandlerInterface
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Module.PlayeAndRecordeHandler.AudioRecorderAndPlayHandler
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AudioPlayerControls(
    parentD_EtateMessageVocale: D_EtateMessageVocale,
    viewModel: ViewModelMessageur,
    audioHandler: AudioRecorderAndPlayHandler,
    isCurrentlyPlaying: Boolean,
    isCurrentlyDownloading: Boolean,
    playbackProgress: AudioHandlerInterface.PlaybackProgress, // FIXED: Use AudioHandlerInterface.PlaybackProgress
    isListened: Boolean,
    latestTimestamp: Long,
    datesHandler: DatesHandler,
    context: Context,
    coroutineScope: CoroutineScope
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause/Stop Button
                PlaybackButton(
                    isCurrentlyPlaying = isCurrentlyPlaying,
                    isCurrentlyDownloading = isCurrentlyDownloading,
                    onPlayClick = {
                        handlePlaybackClick(
                            isCurrentlyPlaying = isCurrentlyPlaying,
                            audioHandler = audioHandler,
                            parentD_EtateMessageVocale = parentD_EtateMessageVocale,
                            isListened = isListened,
                            viewModel = viewModel,
                            datesHandler = datesHandler,
                            context = context,
                            coroutineScope = coroutineScope
                        )
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Progress Bar and Info
                ProgressSection(
                    isCurrentlyDownloading = isCurrentlyDownloading,
                    isCurrentlyPlaying = isCurrentlyPlaying,
                    playbackProgress = playbackProgress,
                    audioHandler = audioHandler,
                    isListened = isListened,
                    latestTimestamp = latestTimestamp,
                    datesHandler = datesHandler
                )
            }
        }
    }
}

@Composable
private fun PlaybackButton(
    isCurrentlyPlaying: Boolean,
    isCurrentlyDownloading: Boolean,
    onPlayClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = when {
            isCurrentlyPlaying -> MaterialTheme.colorScheme.error
            isCurrentlyDownloading -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.primary
        },
        modifier = Modifier.size(48.dp)
    ) {
        IconButton(
            onClick = onPlayClick,
            enabled = !isCurrentlyDownloading
        ) {
            Icon(
                imageVector = when {
                    isCurrentlyDownloading -> Icons.Default.PlayArrow
                    isCurrentlyPlaying -> Icons.Default.Stop
                    else -> Icons.Default.PlayArrow
                },
                contentDescription = when {
                    isCurrentlyDownloading -> "Téléchargement en cours"
                    isCurrentlyPlaying -> "Arrêter la lecture"
                    else -> "Lecture du message vocal"
                },
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ProgressSection(
    isCurrentlyDownloading: Boolean,
    isCurrentlyPlaying: Boolean,
    playbackProgress: AudioHandlerInterface.PlaybackProgress, // FIXED: Use AudioHandlerInterface.PlaybackProgress
    audioHandler: AudioRecorderAndPlayHandler,
    isListened: Boolean,
    latestTimestamp: Long,
    datesHandler: DatesHandler
) {
    Column(
    ) {
        // Progress Bar
        when {
            isCurrentlyDownloading -> {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
            }
            isCurrentlyPlaying && playbackProgress.duration > 0 -> {
                LinearProgressIndicator(
                    progress = { playbackProgress.progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }
            else -> {
                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Time and status display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time display
            TimeDisplay(
                isCurrentlyDownloading = isCurrentlyDownloading,
                isCurrentlyPlaying = isCurrentlyPlaying,
                playbackProgress = playbackProgress,
                audioHandler = audioHandler
            )

            // Status indicator
            StatusIndicator(
                isListened = isListened,
                latestTimestamp = latestTimestamp,
                datesHandler = datesHandler
            )
        }
    }
}

@Composable
private fun TimeDisplay(
    isCurrentlyDownloading: Boolean,
    isCurrentlyPlaying: Boolean,
    playbackProgress: AudioHandlerInterface.PlaybackProgress, // FIXED: Use AudioHandlerInterface.PlaybackProgress
    audioHandler: AudioRecorderAndPlayHandler
) {
    when {
        isCurrentlyDownloading -> {
            Text(
                text = "Téléchargement...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        isCurrentlyPlaying && playbackProgress.duration > 0 -> {
            Text(
                text = "${audioHandler.formatTimeFromMillis(playbackProgress.currentPosition)} / ${audioHandler.formatTimeFromMillis(playbackProgress.duration)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        else -> {
            Text(
                text = "Prêt à lire",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusIndicator(
    isListened: Boolean,
    latestTimestamp: Long,
    datesHandler: DatesHandler
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isListened) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Message écouté",
                tint = Color.Green,
                modifier = Modifier.size(16.dp)
            )
            if (latestTimestamp > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = datesHandler.getDateAndTimString(latestTimestamp).time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Message non écouté",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun handlePlaybackClick(
    isCurrentlyPlaying: Boolean,
    audioHandler: AudioRecorderAndPlayHandler,
    parentD_EtateMessageVocale: D_EtateMessageVocale,
    isListened: Boolean,
    viewModel: ViewModelMessageur,
    datesHandler: DatesHandler,
    context: Context,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        when {
            isCurrentlyPlaying -> {
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
                val playResult = audioHandler.startPlayback(
                    context = context,
                    parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                    onPlaybackComplete = {
                        if (!isListened) {
                            coroutineScope.launch {
                                try {
                                    val newEtate = D_EtateMessageVocale(
                                        parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                                        nom = D_EtateMessageVocale.Nom.ECOUTE,
                                        timestamps = datesHandler.getCurrentTimestamps(),
                                        idParent_1_5_Vendeur = parentD_EtateMessageVocale.idParent_1_5_Vendeur,
                                        nomParent_1_5_Vendeur = parentD_EtateMessageVocale.nomParent_1_5_Vendeur,
                                        relativeAuDataBase = parentD_EtateMessageVocale.relativeAuDataBase,
                                        parentC3_BonAchateVID = parentD_EtateMessageVocale.parentC3_BonAchateVID
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
}
