package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun B_ItemMessagesVocale(
    relative_D_EtateMessageVocale: M17MessageVocale,
    viewModel: ViewModelMessageur,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    list_D_EtateMessageVocale: List<M17MessageVocale>,
    uiState: UiState,
) {
    val activeCurrent_M9AppCompt = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.active_Current_M9AppCompt
    val relative_M9AppCompt = repositorysMainGetter.find_M9AppCompt_By_KeyID(relative_D_EtateMessageVocale.parent_M9AppCompt_KeyID)
    val relative_M8BonVent = repositorysMainGetter.find_M8BonVent_By_KeyID(relative_D_EtateMessageVocale.parent_M8BonVent_KeyID)

    val its_ViewMessage_Du_Active_M9AppCompt = relative_M9AppCompt?.keyID == activeCurrent_M9AppCompt?.keyID

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }
    val audioHandler = viewModel.audioRecorderAndPlayHandler

    val playbackProgress by audioHandler.playbackProgress.collectAsState()

    val relatedBonAchate = remember(relative_D_EtateMessageVocale.parent_M8BonVent_KeyID, uiState.c3_BonAchate) {
        uiState.c3_BonAchate.find { it.keyID == relative_D_EtateMessageVocale.parent_M8BonVent_KeyID }
    }

    val clientName = relatedBonAchate?.parent_M2Client_DebugInfos ?: "Client inconnu"
    val vendorName = relative_D_EtateMessageVocale.parent_M9AppCompt_DebugInfos.takeIf { it.isNotEmpty() } ?: "Vendeur inconnu"

    val isListened = list_D_EtateMessageVocale.any { it.etate == M17MessageVocale.Etate.ECOUTE }
    val isViewed = list_D_EtateMessageVocale.any { it.etate == M17MessageVocale.Etate.VUE }
    val isBeingRecorded = list_D_EtateMessageVocale.any {
        it.etate == M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT
    }
    val isSent = list_D_EtateMessageVocale.any { it.etate == M17MessageVocale.Etate.ENVOYER }

    val latestTimestamp = list_D_EtateMessageVocale.maxByOrNull { it.timestamps }?.timestamps ?: 0L

    val isCurrentlyPlaying = remember(playbackProgress.isPlaying, audioHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioHandler.getCurrentPlaybackSession()?.parentMessageVID == relative_D_EtateMessageVocale.parentMessageVID && playbackProgress.isPlaying
    }

    val isCurrentlyDownloading = remember(playbackProgress.isDownloading, audioHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioHandler.getCurrentPlaybackSession()?.parentMessageVID == relative_D_EtateMessageVocale.parentMessageVID && playbackProgress.isDownloading
    }

    LaunchedEffect(relative_D_EtateMessageVocale.parentMessageVID, isCurrentlyPlaying) {
        if (isCurrentlyPlaying) {
            try {
                while (isCurrentlyPlaying && audioHandler.isPlaying()) {
                    audioHandler.updatePlaybackProgress()
                    delay(100)

                    if (audioHandler.getCurrentPlaybackSession()?.parentMessageVID != relative_D_EtateMessageVocale.parentMessageVID) {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(relative_D_EtateMessageVocale.parentMessageVID) {
        onDispose {
            try {
                val currentSession = audioHandler.getCurrentPlaybackSession()
                if (currentSession?.parentMessageVID == relative_D_EtateMessageVocale.parentMessageVID) {
                    audioHandler.stopPlayback()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column {
        // Main message card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (its_ViewMessage_Du_Active_M9AppCompt) {
                    Arrangement.End // Messages from active account align to the right
                } else {
                    Arrangement.Start // Messages from others align to the left
                }
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(
                            start = if (its_ViewMessage_Du_Active_M9AppCompt) 40.dp else 0.dp,
                            end = if (its_ViewMessage_Du_Active_M9AppCompt) 0.dp else 40.dp
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (its_ViewMessage_Du_Active_M9AppCompt) {
                            // Green bubble for sent messages (like Telegram)
                            when {
                                isListened -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                isViewed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                isBeingRecorded -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            }
                        } else {
                            // Light gray bubble for received messages (like Telegram)
                            when {
                                isListened -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                isViewed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                isBeingRecorded -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                            }
                        }
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (its_ViewMessage_Du_Active_M9AppCompt) 16.dp else 4.dp,
                        bottomEnd = if (its_ViewMessage_Du_Active_M9AppCompt) 4.dp else 16.dp
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        MessageHeader(
                            viewModel = viewModel,
                            clientName = clientName,
                            vendorName = vendorName,
                            messageVID = relative_D_EtateMessageVocale.parentMessageVID,
                            timestamp = relative_D_EtateMessageVocale.timestamps,
                            datesHandler = datesHandler,
                            parentD_EtateMessageVocale = relative_D_EtateMessageVocale,
                            etatesChildKeyIDsList = list_D_EtateMessageVocale,
                            isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt
                        )

                        // FIXED: BonVentInfoCard moved to the top after MessageHeader
                        relative_M8BonVent?.let { m8BonVent ->
                            Spacer(modifier = Modifier.height(12.dp))
                            BonVentInfoCard(
                                m8BonVent = m8BonVent,
                                isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        when {
                            isBeingRecorded && !isSent -> {
                                RecordingIndicator(isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt)
                            }

                            isSent -> {
                                AudioPlayerControls(
                                    parentD_EtateMessageVocale = relative_D_EtateMessageVocale,
                                    viewModel = viewModel,
                                    audioHandler = audioHandler,
                                    isCurrentlyPlaying = isCurrentlyPlaying,
                                    isCurrentlyDownloading = isCurrentlyDownloading,
                                    playbackProgress = playbackProgress,
                                    isListened = isListened,
                                    latestTimestamp = latestTimestamp,
                                    datesHandler = datesHandler,
                                    context = context,
                                    coroutineScope = coroutineScope,
                                    isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt
                                )
                            }
                        }
                    }
                }
            }
        }

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun BonVentInfoCard(
    m8BonVent: M8BonVent,
    isFromActiveAccount: Boolean
) {
    val context = LocalContext.current

    // Updated styling to fit better inside the message bubble
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isFromActiveAccount) {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header with state and debug info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "État: ${m8BonVent.etateActuellementEst.nomArabe}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isFromActiveAccount) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    } else {
                        androidx.compose.ui.graphics.Color(
                            context.getColor(m8BonVent.etateActuellementEst.color)
                        )
                    }
                )

                Text(
                    text = m8BonVent.get_DebugInfos(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFromActiveAccount) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Time information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Début: ${m8BonVent.heurDebutInString}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFromActiveAccount) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Text(
                    text = "Fin: ${m8BonVent.heurFinInString}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFromActiveAccount) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Additional info if vocal message exists
            if (m8BonVent.vocaleKeyID.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (m8BonVent.sonVocaleEstEcoute) {
                            Icons.Default.VolumeUp
                        } else {
                            Icons.Default.VolumeOff
                        },
                        contentDescription = null,
                        tint = if (isFromActiveAccount) {
                            if (m8BonVent.sonVocaleEstEcoute) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                            }
                        } else {
                            if (m8BonVent.sonVocaleEstEcoute) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        },
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (m8BonVent.sonVocaleEstEcoute) "Message vocal écouté" else "Message vocal non écouté",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isFromActiveAccount) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Client information
            if (m8BonVent.parent_M2Client_DebugInfos != "null") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Client: ${m8BonVent.parent_M2Client_DebugInfos}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isFromActiveAccount) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun RecordingIndicator(isFromActiveAccount: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (isFromActiveAccount) {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Enregistrement",
                tint = if (isFromActiveAccount) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.error
                },
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Enregistrement en cours...",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (isFromActiveAccount) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                }
            )
        }
    }
}
