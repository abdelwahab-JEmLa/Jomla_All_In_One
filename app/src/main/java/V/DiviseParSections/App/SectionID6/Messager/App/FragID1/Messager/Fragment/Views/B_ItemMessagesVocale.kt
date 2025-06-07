package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
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
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun B_ItemMessagesVocale(
    parentD_EtateMessageVocale: D_EtateMessageVocale,
    etatesChildKeyIDsList: List<D_EtateMessageVocale>,
    viewModel: ViewModelMessageur,
    uiState: UiState,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }
    val audioHandler = viewModel.audioRecorderAndPlayHandler

    // Collect playback progress from the handler
    val playbackProgress by audioHandler.playbackProgress.collectAsState()

    // Find the related C3_BonAchate based on parentC3_BonAchateVID
    val relatedBonAchate = remember(parentD_EtateMessageVocale.parentC3_BonAchateVID, uiState.c3_BonAchate) {
        uiState.c3_BonAchate.find { it.vid == parentD_EtateMessageVocale.parentC3_BonAchateVID }
    }

    // Get client name from the related BonAchate, fallback to "Client inconnu" if not found
    val clientName = relatedBonAchate?.nomClientConcerned ?: "Client inconnu"

    // Get vendor name from the message data, fallback to "Vendeur inconnu" if empty
    val vendorName = parentD_EtateMessageVocale.nomParent_1_5_Vendeur.takeIf { it.isNotEmpty() } ?: "Vendeur inconnu"

    // Check message states
    val isListened = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.ECOUTE }
    val isViewed = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.VUE }
    val isBeingRecorded = etatesChildKeyIDsList.any {
        it.nom == D_EtateMessageVocale.Nom.EN_COURT_ENREGESTREMENT
    }
    val isSent = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.ENVOYER }

    // Get the latest state timestamp
    val latestTimestamp = etatesChildKeyIDsList.maxByOrNull { it.timestamps }?.timestamps ?: 0L

    // Check if this specific message is currently playing
    val isCurrentlyPlaying = remember(playbackProgress.isPlaying, audioHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioHandler.getCurrentPlaybackSession()?.parentMessageVID == parentD_EtateMessageVocale.parentMessageVID && playbackProgress.isPlaying
    }

    // Check if this message is currently downloading
    val isCurrentlyDownloading = remember(playbackProgress.isDownloading, audioHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioHandler.getCurrentPlaybackSession()?.parentMessageVID == parentD_EtateMessageVocale.parentMessageVID && playbackProgress.isDownloading
    }

    // Update progress periodically while playing
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
                e.printStackTrace()
            }
        }
    }

    // Cleanup when leaving composition
    DisposableEffect(parentD_EtateMessageVocale.parentMessageVID) {
        onDispose {
            try {
                val currentSession = audioHandler.getCurrentPlaybackSession()
                if (currentSession?.parentMessageVID == parentD_EtateMessageVocale.parentMessageVID) {
                    audioHandler.stopPlayback()
                }
            } catch (e: Exception) {
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
                // Check if this message belongs to parent account 1 (current user)
                parentD_EtateMessageVocale.idParent_1_5_Vendeur == 1L -> {
                    // Red tint for current user's messages
                    when {
                        isListened -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                        isViewed -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        isBeingRecorded -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    }
                }
                else -> {
                    // Blue tint for other users' messages
                    when {
                        isListened -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        isViewed -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                        isBeingRecorded -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    }
                }
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Improved message header with client name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice message icon
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.GraphicEq,
                            contentDescription = "Message vocal",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        // Display client name as primary information
                        Text(
                            text = clientName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Display vendor name as secondary information
                        Text(
                            text = "Vendeur: $vendorName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Tertiary info: Message ID and type
                        Text(
                            text = "Message vocal #${parentD_EtateMessageVocale.parentMessageVID}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Timestamp with better formatting
                Text(
                    text = datesHandler.getDateAndTimString(parentD_EtateMessageVocale.timestamps).time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show different UI based on message state with improved visuals
            when {
                isBeingRecorded && !isSent -> {
                    // Recording in progress indicator with better visual feedback
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Enregistrement",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enregistrement en cours...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                isSent -> {
                    // Enhanced audio player controls
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
                                // Enhanced Play/Pause/Stop Button
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
                                        onClick = {
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

                                Spacer(modifier = Modifier.width(12.dp))

                                // Enhanced Progress Bar
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
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

                                        // Status indicator
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
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Unknown state with better visual feedback
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "État inconnu",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "État du message inconnu",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }

    // Improved divider
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}
