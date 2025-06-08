package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
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
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
  //<--
  //TODO(1): enlve les commentaires
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
            // Message header with client name
            MessageHeader(
                clientName = clientName,
                vendorName = vendorName,
                messageVID = parentD_EtateMessageVocale.parentMessageVID,
                timestamp = parentD_EtateMessageVocale.timestamps,
                datesHandler = datesHandler
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Show different UI based on message state
            when {
                isBeingRecorded && !isSent -> {
                    RecordingIndicator()
                }

                isSent -> {
                    AudioPlayerControls(
                        parentD_EtateMessageVocale = parentD_EtateMessageVocale,
                        viewModel = viewModel,
                        audioHandler = audioHandler,
                        isCurrentlyPlaying = isCurrentlyPlaying,
                        isCurrentlyDownloading = isCurrentlyDownloading,
                        playbackProgress = playbackProgress,
                        isListened = isListened,
                        latestTimestamp = latestTimestamp,
                        datesHandler = datesHandler,
                        context = context,
                        coroutineScope = coroutineScope
                    )
                }
            }
        }
    }

    // Divider
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
private fun MessageHeader(
    clientName: String,
    vendorName: String,
    messageVID: Long,
    timestamp: Long,
    datesHandler: DatesHandler
) {
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
                    text = "Message vocal #$messageVID",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Timestamp with better formatting
        Text(
            text = datesHandler.getDateAndTimString(timestamp).time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RecordingIndicator() {
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
